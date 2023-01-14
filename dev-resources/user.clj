;; Copyright © 2023 Greg Haskins.  All rights reserved

(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [eftest.runner :refer [find-tests run-tests]]
            [integrant.repl :refer [clear go halt prep init reset reset-all]]
            [reminderbot.config :as config]
            [reminderbot.core :as core]))

(integrant.repl/set-prep! #(config/merge-components core/components))
