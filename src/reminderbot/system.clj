;; Copyright © 2023 Greg Haskins.  All rights reserved

(ns reminderbot.system
  (:require [integrant.core :as ig]
            [reminderbot.config :as config]))

(defn start
  [components & args]
  (let [c (apply config/merge-components components args)]
    (ig/init (ig/prep c))))

(defn stop
  [instance]
  (ig/halt! instance))
