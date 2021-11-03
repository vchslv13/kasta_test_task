(ns kasta-test-task.consumer
  (:require
   [clojure.core.async :refer [thread]]
   [jackdaw.client :as jc]
   [kasta-test-task.services :refer [list-filters add-message]]
   [kasta-test-task.topics :refer [topics init-topics]]
   [kasta-test-task.config :refer [kafka-uri]]))

(def consumer-config
  {"bootstrap.servers" kafka-uri
   "key.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
   "group.id"  "kasta-test-task.consumer.consumer"})

(defn matches-query
  [message filter]
  (let [q (:q filter)
        regexp (re-pattern (str "(?i)" q))]
    (re-find regexp message)))

(defn applying-filter-ids
  [message filters]
  (->> filters
       (filter #(matches-query message %))
       (map :_id)))

(defn message-with-ids [messages filters]
  (->> messages
       (map (fn [msg] [msg (applying-filter-ids msg filters)]))
       (filter #(seq (second %)))))

(defn process-messages [messages]
  (doseq [[message ids] (message-with-ids messages (list-filters))]
    (add-message ids message)))

(defn consumer []
  (thread
    (init-topics (list-filters))
    (with-open [consumer (jc/consumer consumer-config)]
      (jc/subscribe consumer @topics)
      (while true
        (let [old-topics @topics
              messages (jc/poll consumer 500)
              new-topics @topics]
          (prn messages)
          (process-messages (map :value messages))
          (when (not= old-topics new-topics)
            (jc/subscribe consumer @topics)))))))
