(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [eftest.runner :refer [find-tests run-tests]]
            [integrant.repl :refer [clear go halt prep init reset reset-all]]
            [integrant.core :as ig]))
