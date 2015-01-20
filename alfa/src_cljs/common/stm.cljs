(ns common.stm
	(:require
		[cljs.reader :refer [read-string]]
		[ajax.core :as http]
		[reagent.ratom :as ratom]))

(defprotocol IStateManagement
	"State management of the application, especially managing users and available drills"
	(get-current-user [this] "Get the current user from the data")
	(get-user [this user-map] "Get a particular user data")
	(user-exists? [this user-map] "Check the existence of a particular user")
	(all-users [this] "Get all available users in app")
	(set-current-user! [this] [this user-map] "Set the current user with provided user-map")
	(add-user! [this user-map] "Add a new user to the system")
	(init-drills! [this available-drills] "Initialise the app by mounting the current drills")
	(get-drills [this] [this available-drills] "Get the available drills")
	(get-drill [this which-drill?] "Get the specific drill")
	(set-current-drill! [this which-drill?] "Set the current drill")
	(get-current-drill [this]))

(defrecord Database [dbname]
	IStateManagement
	(get-current-user [this]
		(let [{:keys [dbname]} this
					data (->> (str dbname "/" "current-user")
										(.getItem js/localStorage))]
			(if data (read-string data) nil)))
	(set-current-user! [this user-map]
		(let [{:keys [dbname]} this]
			(.setItem js/localStorage
								(str dbname "/" "current-user")
								user-map)))
	(all-users [this]
		(let [{:keys [dbname]} this
					data (.getItem js/localStorage (str dbname "/" "users"))]
			(if data (read-string data) [])))
	;; TODO check the username and password against the data in zeniusnet
	(user-exists? [this user-map]
		(let [{:keys [username]} user-map
					users-data (all-users this)]
			(->> (map :username users-data)
					 (some #{username}))))
	(get-user [this user-map]
		(let [{:keys [username]} user-map
					users-data (all-users this)]
			(first (-> #(= username (:username %))
								 (filter users-data)))))
	(add-user! [this user-map]
		(let [{:keys [dbname]} this
					old-users (all-users this)]
			(->> (assoc user-map :answer 0 :correct 0)
					 (conj old-users)
					 (.setItem js/localStorage (str dbname "/users")))))
	(init-drills! [this available-drills]
		(let [{:keys [dbname]} this]
			(doseq [drill available-drills]
				(http/ajax-request
					{:uri             (str "data/" drill ".edn")
					 :method          :get
					 :response-format (http/edn-response-format)
					 :handler         (fn [[_ data]]
															(.setItem js/localStorage
																				(str dbname "/drills/" drill)
																				data))}))))
	(get-drill [this which-drill?]
		(let [{:keys [dbname]} this
					data (.getItem js/localStorage (str dbname "/drills/" which-drill?))]
			(if data (read-string data) nil)))
	(get-drills [this available-drills]
		(map #(get-drill this %) available-drills))
	(set-current-drill! [this which-drill?]
		(let [{:keys [dbname]} this]
			(.setItem js/localStorage (str dbname "/current-drill")
								(get-drill this which-drill?)))))

(defprotocol IConfig
	(set-available-drills! [this conf]))

(extend-type ratom/RAtom
	IStateManagement
	(get-current-user [this]
		(:current-user @this))
	(set-current-user! [this]
		(->> (get-current-user (:database @this))
				 (swap! this assoc :current-user)))
	(all-users [this]
		(:users @this))
	(add-user! [this user-map]
		(let [old-users (all-users this)]
			(->> (assoc user-map :answer 0 :correct 0)
					 (conj old-users)
					 (swap! this assoc :users))))
	(init-drills! [this available-drills]
		(let [{:keys [database]} @this]
			(->> (get-drills database available-drills)
					 (swap! this assoc :drills))))
	(get-drills [this]
		(:drills @this))
	(get-drill [this which-drill?]
		(nth (:drills @this)
				 (condp = which-drill?
					 "one" 0 "two" 1 "three" 2)))
	(set-current-drill! [this which-drill?]
		(->> (get-drill this which-drill?)
				 (swap! this assoc :current-drill)))
	(get-current-drill [this]
		(:current-drill @this))
	IConfig
	(set-available-drills! [this conf]
		(let [drills (:available-drills conf)]
			(swap! this assoc :available-drills drills))))

(defn local-database
	"A constructor for local storage database, essentially creating a keyspace for the intended data"
	[dbname]
	(Database. dbname))



















