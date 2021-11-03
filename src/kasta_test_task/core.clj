(ns kasta-test-task.core
  (:require [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :refer [wrap-reload]]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.coercion.spec]
            [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [reitit.ring.middleware.muuntaja
             :refer [format-middleware]
             :rename {format-middleware muuntaja-format-middleware}]
            [muuntaja.core]
            [kasta-test-task.api :as api]
            [kasta-test-task.consumer :refer [consumer]])
  (:gen-class))

(def routes
  [["/api"
    ["/status/" {:get {:handler api/status
                       :responses {200 {:body ::api/status-output}}}}]
    ["/filter" {:post {:handler api/filter-create
                       :parameters {:body ::api/filter-create-input}
                       :responses {201 {:body ::api/filter-create-output}
                                   400 {:body string?}}}
                :get {:handler api/filter-list
                      :parameters {:query ::api/filter-retrieve-query}
                      :query-params ::api/filter-retrieve-query
                      :responses {200 {:body ::api/filter-get-output}
                                  404 {:body string?}}}
                :delete {:handler api/filter-delete
                         :parameters {:body ::api/filter-delete-input}
                         :responses {204 {:body string?}
                                     404 {:body string?}}}}]]
   ["" {:no-doc true}
    ["/swagger/*" {:get (swagger-ui/create-swagger-ui-handler)}]
    ["/swagger.json" {:get (swagger/create-swagger-handler)}]]])

(def router-options
  {:data {:coercion reitit.coercion.spec/coercion
          :muuntaja muuntaja.core/instance
          :middleware [muuntaja-format-middleware
                       parameters-middleware
                       coercion/coerce-exceptions-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-response-middleware]}})


(def app
  (ring/ring-handler
   (ring/router routes router-options)
   (ring/create-default-handler)))

(defn -main [& args]
  (consumer)
  (let [handler (wrap-reload #'app)]
    (run-server handler {:port 8080})))
