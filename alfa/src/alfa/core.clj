(ns alfa.core
	(:gen-class)
	(:require [compojure.core :refer :all]
						[compojure.route :refer [not-found resources]]
						[noir.response :refer [edn json jsonp]]
						[zenpack.core :as zen]
						[alfa.config :refer [conf]]))

(defroutes
	main-routes
	(GET "/" [] "Welcome")
	(GET "/api" [req]
			 (json {:data "This is a sample edn response"}))
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
	(run 4000))




