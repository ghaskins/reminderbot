;; Copyright © 2023 Greg Haskins.  All rights reserved

(ns reminderbot.parse
  (:require [clojure.java.io :as io]
            [taoensso.timbre :as log]
            [instaparse.core :as insta]))

(defn parse
  [parser expression]
  (log/trace "parsing:" expression)
  (let [result (insta/parse parser expression)]
    (if (insta/failure? result)
      (do
        (log/error (insta/get-failure result))
        (throw (ex-info "parse failure" result)))
      (do
        (log/trace "successful parse:" result)
        result))))

(defn remindme
  [expression]
  (let [parser (insta/parser (io/resource "remindme.ebnf"))]
    (parse parser expression)))
