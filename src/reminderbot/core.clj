;; Copyright © 2023 Greg Haskins.  All rights reserved

(ns reminderbot.core
  (:require [clojure.core.async :refer [go-loop <! >!!] :as async]
            [taoensso.timbre :as log]
            [integrant.core :as ig]
            [reminderbot.slack-api :as slack]
            [reminderbot.temporal :as temporal]
            [reminderbot.commands.core :as commands]
            [reminderbot.commands.remindme]))

(defmulti dispatch (fn [_ {:keys [type]}] type))

(defmethod dispatch "hello"
  [ctx payload]
  (log/trace "received hello:" payload))

(defmethod dispatch "slash_commands"
  [ctx payload]
  (commands/dispatch ctx payload))

(defmethod dispatch :default
  [ctx payload]
  (log/trace "unknown msg:" payload))

(defn ack-event
  [{:keys [tx]} {:keys [envelope_id] :as m} response]
  (when (some? envelope_id)
    (>!! tx {:envelope_id envelope_id
             :payload response})))

(defn start
  [ctx]
  (let [{:keys [rx tx]} (slack/open-connection ctx)
        ctx (assoc ctx :tx tx)]
    (log/info "Starting event-loop")
    (go-loop []
      (if-let [event (<! rx)]
        (let [response (dispatch ctx event)]
          (ack-event ctx event response)
          (recur))
        (do
          (log/info "Shutting down event-loop"))))
    ctx))

(defn stop
  [{:keys [tx]}]
  (async/close! tx))

(defmethod ig/init-key ::service [_ params] (start params))
(defmethod ig/halt-key! ::service [_ params] (stop params))

(def components
  {::temporal/client {}
   ::temporal/worker {:wf-client (ig/ref ::temporal/client)}
   ::service         {:wf-client (ig/ref ::temporal/client)}})
