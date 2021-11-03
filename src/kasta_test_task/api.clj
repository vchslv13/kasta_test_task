(ns kasta-test-task.api
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :refer [response bad-request not-found]]
            [kasta-test-task.services :as services]
            [kasta-test-task.utils :refer [no-content]]))

(s/def ::status string?)
(s/def ::status-output (s/keys :req-un [::status]))
(defn status [_]
  (response {:status "OK"}))

(defn coerce-filter
  "Workaround for output as I was not able to turn on response coercion."
  [filter]
  (update filter :_id str))

(s/def ::_id string?)
(s/def ::topic string?)
(s/def ::q string?)
(s/def ::filter-create-input (s/keys :req-un [::topic ::q]))
(s/def ::filter-create-output (s/keys :req-un [::topic ::q ::_id]))
(defn filter-create [request]
  (let [filter-data (get-in request [:parameters :body])
        filter (services/create-filter filter-data)]
    (if filter
      {:status 201 :headers {} :body (coerce-filter filter)}
      (bad-request "Filter already exist."))))

(s/def ::id string?)
(s/def ::messages (s/coll-of string? :into []))
(s/def ::filter-retrieve-query (s/keys :opt-un [::id]))
(s/def ::filter-retrieve-output (s/keys :req-un [::topic ::q ::_id]))
(s/def ::filter-list-item (s/keys :req-un [::topic ::q ::_id]))
(s/def ::filter-list-output (s/coll-of ::filter-list-item :into []))
(s/def ::filter-get-output (s/or :list ::filter-list-output :retrieve ::filter-retrieve-output))
(defn filter-list [request]
  (let [id (get-in request [:parameters :query :id])]
    (if id
      (let [filter (services/get-filter id)]
        (if filter (response (coerce-filter filter)) (not-found "Filter not found.")))
      (response (map coerce-filter (services/list-filters))))))

(s/def ::filter-delete-input (s/keys :req-un [::id]))
(defn filter-delete [request]
  (let [id (get-in request [:parameters :body :id])]
    (if (services/delete-filter id)
      (no-content "Filter deleted.")
      (not-found "Filter not found."))))
