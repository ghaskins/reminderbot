;; Copyright © 2023 Greg Haskins.  All rights reserved

(ns reminderbot.main
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.core :as appenders]
            [promesa.core :as p]
            [reminderbot.config :as config]
            [reminderbot.system :as system]
            [reminderbot.core :as core])
  (:gen-class))

(log/set-config!
 {:level :trace
  :appenders {:println (appenders/println-appender {:stream :auto})}})

(def options
  [["-h" "--help"]
   ["-v" "--version" "Print the version and exit"]])

(defn exit
  [status msg & args]
  (apply println msg args)
  status)

(defn print-version [] (str "reminderbot version: v" (System/getProperty "reminderbot.version")))

(defn prep-usage [msg] (->> msg flatten (string/join \newline)))

(defn usage
  [options-summary]
  (prep-usage [(print-version)
               ""
               "Usage: reminderbot [options]"
               ""
               "Options:"
               options-summary]))

(defn uncaught-handler
  [f]
  (Thread/setDefaultUncaughtExceptionHandler
   (reify Thread$UncaughtExceptionHandler
     (uncaughtException [_ thread ex]
       (f thread ex)))))

(defn exec
  [{:keys [config] :as _options}]
  (p/create
   (fn [_resolve reject]
     (uncaught-handler
      (fn [^Thread thread ex]
        (log/error "uncaught exception in thread:" (.getName thread))
        (reject ex)))
     (system/start core/components config)
     (log/info "System running"))))

(defn -app
  [& args]
  (let [{{:keys [help] :as options} :options :keys [errors summary]} (parse-opts args options)
        c (config/get)]
    (cond

      help
      (exit 0 (usage summary))

      (not= errors nil)
      (exit -1 "Error: " (string/join errors))

      (:version options)
      (exit 0 (print-version))

      :else
      (try
        @(exec (assoc options :config c))
        (exit 0 "")
        (catch Exception e
          (log/error e)
          (exit -1 (ex-message e)))))))

(defn -main
  [& args]
  (System/exit (apply -app args)))

