(ns alfa.core
	(:require [compojure.core :refer :all]
						[compojure.handler :refer [site]]
						[compojure.route :refer [not-found resources]]
						[noir.response :refer [edn json jsonp]]
						[alfa.config :refer [conf]]
						[selmer.parser :refer [render-file]]
						[immutant.web :as web]
						[ring.middleware.edn :refer [wrap-edn-params]]
						[org.httpkit.server :as http]
						[ring.middleware.defaults :refer :all]
						[ring.middleware.resource :refer [wrap-resource]]))

(defroutes
	main-routes
	(GET "/" []
			 (do (println "welcome")
					 "Welcome"))
	(GET "/api" [req]
			 (json {:data "This is a sample edn response"}))
	(GET "/zeneng" req
			 (do (println "zeneng")
					 (render-file "templates/base.html"
												{})))
	(POST "/woli" req
				(do (println (str (:params req)))
						(edn {:name "wolipopo"}))))

(defonce server (atom nil))

(defn stop-server []
	(when-not (nil? @server)
		;; graceful shutdown: wait 100ms for existing requests to be finished
		;; :timeout is optional, when no timeout, stop immediately
		(@server :timeout 100)
		(reset! server nil)))

(defn httpkit []
	(reset! server
					(-> (wrap-resource main-routes "public")
							(wrap-edn-params)
							(http/run-server {:port 3000}))))

(defn -main
	[& args]
	(do (println "Web server runs in port 3000")
			(httpkit)))




