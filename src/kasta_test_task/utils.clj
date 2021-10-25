(ns kasta-test-task.utils)

(defn no-content
  "Returns a 204 'no content' response."
  {:added "1.7"}
  [body]
  {:status  204
   :headers {}
   :body    body})
