;; Copyright © 2023 Greg Haskins.  All rights reserved

(ns reminderbot.slack-api
  (:require [clojure.core.async :refer [<! >!! go-loop] :as async]
            [promesa.core :as p]
            [clj-http.client :as http]
            [taoensso.timbre :as log]
            [hato.websocket :as ws]
            [cheshire.core :as json]))

(defn- http-post
  [url args]
  (p/create
   (fn [resolve reject]
     (http/post url
                (assoc args
                       :async? true
                       :as :json
                       :content-type :json)
                resolve
                reject))))

(defn invoke
  ([token op] (invoke token op {}))
  ([token op params]
   (log/debug "invoke:" op params)
   (-> (http-post (str "https://slack.com/api/" op)
                  {:headers {"Authorization" (str "Bearer " token)}
                   :form-params params})
       (p/then (fn [{{:keys [ok error] :as body} :body}]
                 (if-not ok
                   (do
                     (log/error error)
                     (p/rejected (ex-info error {})))
                   (do
                     (log/debug "response:" body)
                     body)))))))

(defn- connect-ws
  [url ch]
  (ws/websocket url
                {:on-message (fn [ws msg last?]
                               (let [x (json/parse-string (str msg) true)]
                                 (log/trace "on-message:" x)
                                 (>!! ch x)))
                 :on-close   (fn [ws status reason]
                               (log/trace "on-close:")
                               (async/close! ch))}))

(defn- xmit-listener
  [ch ws]
  (go-loop []
    (if-let [m (<! ch)]
      (let [m (json/generate-string m)]
        (log/trace "tx:" m)
        (ws/send! ws m)
        (recur))
      (do
        (ws/close! ws)))))

(defn open-connection
  [{:keys [api-token]}]
  (let [rx (async/chan)
        tx (async/chan)]
    @(-> (invoke api-token "apps.connections.open")
         (p/then (fn [{:keys [url]}]
                   (connect-ws url rx)))
         (p/then (partial xmit-listener tx)))
    {:rx rx :tx tx}))
