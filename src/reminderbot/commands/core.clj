;; Copyright © 2023 Greg Haskins.  All rights reserved

(ns reminderbot.commands.core)

(defmulti dispatch (fn [_ {{:keys [command]} :payload}] command))
