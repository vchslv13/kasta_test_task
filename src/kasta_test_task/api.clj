(ns kasta-test-task.api
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :refer [response bad-request]]
            [kasta-test-task.services :as services]))

(s/def ::status string?)
(s/def ::status-output (s/keys :req-un [::status]))
(defn status [_]
  (response {:status "OK"}))

(s/def ::_id string?)
(s/def ::topic string?)
(s/def ::q string?)
(s/def ::filter-create-input (s/keys :req-un [::topic ::q]))
(s/def ::filter-create-output (s/keys :req-un [::topic ::q ::_id]))
(defn filter-create [request]
  (let [filter-data (get-in request [:parameters :body])
        filter (services/create-filter filter-data)]
    (if filter
      (response (update filter :_id str))
      (bad-request "Filter already exist."))))
