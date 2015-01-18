(ns beta.init
  (:require
    [reagent.core :refer [atom render-component]]
    [common.state :refer [app-state]]
    [common.db :as db :refer [make-local-storage]]))


(defn start
  [app]
  (do (->> (make-local-storage "app-database")
           (swap! app assoc :database))
      (let [dbase (cur app [:database])]
        (if (user/current? app)
          (swap! app merge
                 {:user })))))









