(defproject reminderbot "0.1.0-SNAPSHOT"
  :description "A bot for Slack for sending reminders to yourself in the future "
  :url "https://github.com/ghaskins/reminderbot"
  :plugins [[lein-cljfmt "0.9.0"]]
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/core.async "1.6.673"]
                 [org.clojure/tools.cli "1.0.214"]
                 [environ "1.2.0"]
                 [integrant "0.8.0"]
                 [hato "0.9.0"]
                 [clj-http "3.12.3"]
                 [cheshire "5.11.0"]
                 [clojure.java-time "1.2.0"]
                 [com.zoho/hawking "0.1.7"]
                 [com.taoensso/timbre "6.0.4"]
                 [com.fzakaria/slf4j-timbre "0.3.21"]
                 [org.slf4j/jul-to-slf4j "2.0.5"]
                 [org.slf4j/jcl-over-slf4j "2.0.5"]
                 [org.slf4j/log4j-over-slf4j "2.0.5"]
                 [io.github.manetu/temporal-sdk "0.12.1"]]
  :main ^:skip-aot reminderbot.main

  :target-path "target/%s"
  :repl-options {:init-ns user}

  :profiles {:dev     {:dependencies   [[org.clojure/tools.namespace "1.3.0"]
                                        [criterium "0.4.6"]
                                        [eftest "0.6.0"]
                                        [integrant/repl "0.3.2"]]}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
