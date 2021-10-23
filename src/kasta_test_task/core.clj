(ns kasta-test-task.core
  (:require [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :refer [wrap-reload]]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.coercion.spec]
            [reitit.ring.middleware.muuntaja
             :refer [format-middleware]
             :rename {format-middleware muuntaja-format-middleware}]
            [muuntaja.core]
            [kasta-test-task.api :as api])
  (:gen-class))

(def routes
  [["/api"
    ["/status/" {:get {:handler api/status
                       :responses {200 {:body ::api/status-output}}}}]]
   ["" {:no-doc true}
    ["/swagger/*" {:get (swagger-ui/create-swagger-ui-handler)}]
    ["/swagger.json" {:get (swagger/create-swagger-handler)}]]])

(def router-options
  {:data {:coercion reitit.coercion.spec/coercion
          :muuntaja muuntaja.core/instance
          :middleware [muuntaja-format-middleware
                       coercion/coerce-exceptions-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-response-middleware]}})


(def app
  (ring/ring-handler
   (ring/router routes router-options)
   (ring/create-default-handler)))

(defn -main [& args]
  (let [handler (wrap-reload #'app)]
    (run-server handler {:port 8080})))
