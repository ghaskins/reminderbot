(defproject temporal-clojure-demo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [environ "1.2.0"]
                 [integrant "0.8.0"]]
  :main ^:skip-aot temporal-clojure-demo.core

  :target-path "target/%s"
  :repl-options {:init-ns user}

  :profiles {:dev     {:dependencies   [[org.clojure/tools.namespace "1.3.0"]
                                        [criterium "0.4.6"]
                                        [eftest "0.6.0"]
                                        [integrant/repl "0.3.2"]]}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
