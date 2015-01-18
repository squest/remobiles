(ns common.user
  (:require [common.db :as db :refer [make-local-storage]]
            [common.state :refer [app-state]]
            [reagent.cursor :refer [cur]]))

(defn all-users
  [app]
  (let [dbase (cur app [:database])]
    (db/find-items @dbase (fn [e] (= "tUser" (:dtype e))))))

(defn create-user!
  [app username password]
  (let [dbase (cur app [:database])]
    (db/put-item! @dbase
                  {:username username
                   :password password
                   :correct 0
                   :total 0})))

(defn get-user
  [app uname]
  (let [dbase (cur app [:database])]
    (db/find-item @dbase
                  (fn [{:keys [dtype username]}]
                    (and (= dtype "tUser")
                         (= username uname))))))

(defn current-user
  [app]
  (let [dbase (cur app [:database])]
    ))
