(ns alfa.core
	(:gen-class)
	(:require [compojure.core :refer :all]
						[compojure.route :refer [not-found resources]]
						[noir.response :refer [edn json jsonp]]
						[zenpack.core :as zen]
						[alfa.config :refer [conf]]
						[selmer.parser :refer [render-file]]))

(defroutes
	main-routes
	(GET "/" [] "Welcome")
	(GET "/api" [req]
			 (json {:data "This is a sample edn response"}))
	(GET "/zeneng" req
			 (do (println "zeneng")
					 (render-file "templates/base.html"
												{})))
	(POST "/woli" [req]
				(do (println (str (:body req)))
						(edn {:name "wolipopo"})))
	(not-found "Nothing found here"))

(defonce server (atom nil))

(defn run
	[port]
	(zen/start-site server main-routes port))

(defn stop
	[]
	(zen/stop-site server))

(defn -main
	[& args]
	(run 3000))




