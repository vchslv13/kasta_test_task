(ns kasta-test-task.config
  (:require [environ.core :refer [env]]))

(def mongo-uri (env :mongo-uri "mongodb://root:pass@mongo:27017/admin"))
