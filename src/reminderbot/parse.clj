;; Copyright © 2023 Greg Haskins.  All rights reserved

(ns reminderbot.parse
  (:require [clojure.string :as string]
            [taoensso.timbre :as log]
            [reminderbot.hawking :as hawking]))

(defn remindme
  [expression]
  (let [{:keys [parser-start-index parser-end-index] {:keys [end]} :date-range :as r} (hawking/parse expression)
        time-portion (subs expression parser-start-index parser-end-index)
        phrase (string/trim (string/replace expression time-portion ""))]
    (log/debug "parsed-> input:" expression "output:" r "time-portion:" time-portion)
    {:phrase phrase :deadline end}))
