(defproject kasta-test-task "0.1.0-SNAPSHOT"
  :description "Kasta test project"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/core.async "1.3.618"]
                 [cljfmt "0.7.0"]
                 [ring/ring-devel "1.9.3"]
                 [ring/ring-core "1.9.3"]
                 [ring/ring-json "0.5.1"]
                 [http-kit "2.5.3"]
                 [metosin/reitit "0.5.13"]
                 [com.novemberain/monger "3.1.0"]
                 [environ "1.2.0"]
                 [fundingcircle/jackdaw "0.8.0"]]
  :plugins [[lein-environ "1.2.0"]]
  :main ^:skip-aot kasta-test-task.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
