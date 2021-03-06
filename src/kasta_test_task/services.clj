(ns kasta-test-task.services
  (:require [kasta-test-task.config :as config]
            [kasta-test-task.topics :refer [add-topic]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer [$push]])
  (:import org.bson.types.ObjectId))

(defn db []
  (let [{:keys [db]} (mg/connect-via-uri config/mongo-uri)] db))

(defn with-id [document]
  (let [oid (ObjectId.)]
    (assoc document :_id oid)))

(defn create-filter [filter]
  (let [db (db)
        coll "filters"]
    (when-not (mc/any? db coll filter)
      (let [created (mc/insert-and-return db coll (with-id filter))]
        (add-topic created)
        created))))

(defn list-filters []
  (mc/find-maps (db) "filters" {} [:q :topic]))

(defn get-filter [id]
  (let [filter (mc/find-map-by-id (db) "filters" (ObjectId. id))]
    (if filter
      (assoc filter :messages (filter :messages []))
      filter)))

(defn delete-filter [id]
  (let [result (mc/remove-by-id (db) "filters" (ObjectId. id))
        nRemoved (.getN result)]
    (not (zero? nRemoved))))

(defn add-message [ids message]
  (mc/update-by-ids (db) "filters" ids {$push {:messages message}}))
