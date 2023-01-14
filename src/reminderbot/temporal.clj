;; Copyright © 2023 Greg Haskins.  All rights reserved

(ns reminderbot.temporal
  (:require [integrant.core :as ig]
            [taoensso.timbre :as log]
            [temporal.client.core :as c]
            [temporal.client.worker :as w]))

(defn client-create
  [{:keys [workflow-hostport workflow-namespace] :as params}]
  (c/create-client {:target workflow-hostport :namespace workflow-namespace}))

(defmethod ig/init-key ::client [_ params] (client-create params))

(defn worker-start
  [{:keys [wf-client workflow-queuename] :as ctx}]
  (log/info "Starting Temporal worker")
  (w/start wf-client {:task-queue workflow-queuename :ctx ctx}))

(defn worker-stop
  [instance]
  (log/info "Stopping Temporal worker")
  (w/stop instance))

(defmethod ig/init-key ::worker [_ params] (worker-start params))
(defmethod ig/halt-key! ::worker [_ instance] (worker-stop instance))
