(ns kasta-test-task.topics)

(def topics (atom []))

(defn init-topics [filters]
  (reset! topics (map #(hash-map :topic-name (:topic %)) filters)))

(defn add-topic [filter]
  (swap! topics #(conj % {:topic-name (:topic filter)})))