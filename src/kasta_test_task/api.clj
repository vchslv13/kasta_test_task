(ns kasta-test-task.api
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :refer [response]]))

(s/def ::status string?)
(s/def ::status-output (s/keys :req-un [::status]))
(defn status [_]
  (response {:status "OK"}))