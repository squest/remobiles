(ns common.stm
	(:require
		[cljs.reader :refer [read-string]]
		[ajax.core :as http]))

(defprotocol StateManagement
	"Managing the state of the application"
	(get-current-user [this] "Get the current user from the data")
	(user-exists? [this user-map] "Check the existence of a particular user")
	(all-users [this] "Get all available users in app")
	(set-current-user! [this user-map] "Set the current user with provided user-map")
	(add-user! [this user-map] "Add a new user to the system")
	(init-drills! [this available-drills] "Initialise the app by mounting the current drills")
	(get-drills [this available-drills] "Get the available drills")
	(get-drill [this which-drill?] "Get the specific drill")
	(set-current-drill! [this which-drill?] "Set the current drill"))

(defrecord Database [dbname]
	StateManagement
	(get-current-user [this]
		(let [{:keys [dbname]} this
					data (.getItem js/localStorage (str dbname "/" "current-user"))]
			(if data (read-string data) nil)))
	(set-current-user! [this user-map]
		(let [{:keys [dbname]} this]
			(.setItem js/localStorage (str dbname "/" "current-user")
								user-map)))
	(all-users [this]
		(let [{:keys [dbname]} this
					data (.getItem js/localStorage (str dbname "/" "users"))]
			(if data (read-string data) nil)))
	;; TODO check the username and password against the data in zeniusnet
	(user-exists? [this user-map]
		(let [{:keys [dbname]} this
					{:keys [username]} user-map
					users-data (all-users this)]
			(some #{username} (map :username users-data))))
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
		(reduce #(assoc %1 %2 (get-drill this %2))
						{} available-drills))
	(set-current-drill! [this which-drill?]
		(let [{:keys [dbname]} this]
			(.setItem js/localStorage (str dbname "/current-drill")
								(get-drill this which-drill?)))))

(defrecord AppState [reatom]
	StateManagement
	(get-current-user [this]
		(let [{:keys [reatom]} this]
			(:current-user @reatom)))
	(set-current-user! [this user-map]
		(let [{:keys [reatom]} this]
			(swap! reatom assoc :current-user user-map)))
	(all-users [this]
		(let [{:keys [reatom]} this]
			(:users @reatom)))
	(add-user! [this user-map]
		(let [{:keys [reatom]} this
					old-users (all-users this)]
			(swap! reatom assoc :users (conj old-users user-map))))
	(init-drills! [this available-drills]
		(let [{:keys [reatom]} this
					{:keys [database available-drills]} @reatom]
			(swap! reatom assoc :drills (get-drills database available-drills))))
	(get-drills [this available-drills]
		(let [{:keys [reatom]} this]
			(:drills @reatom)))
	(get-drill [this which-drill?]
		(let [{:keys [reatom]} this]
			(get (:drills @reatom) which-drill?)))
	(set-current-drill! [this which-drill?]
		(let [{:keys [reatom]} this]
			(swap! reatom assoc :current-drill (get-drill this which-drill?)))))

(extend-type reagent.core/atom
	StateManagement
	(get-current-user [this]
		(:current-user @this))
	(set-current-user! [this user-map]
		(swap! this assoc :current-user user-map))
	(all-users [this]
		(:users @this))
	(add-user! [this user-map]
		(let [old-users (all-users this)]
			(->> (assoc user-map :answer 0 :correct 0)
					 (conj old-users)
					 (swap! this assoc :users))))
	(init-drills! [this available-drills]
		(let [{:keys [database available-drills]} @this]
			(swap! this assoc :drills (get-drills database available-drills))))
	(get-drills [this available-drills]
		(:drills @this))
	(get-drill [this which-drill?]
		(get (:drills @this) which-drill?))
	(set-current-drill! [this which-drill?]
		(swap! this assoc :current-drill (get-drill this which-drill?))))

(defn make-app-state
	[reatom]
	(AppState. reatom))

(defn make-local-storage
	[dbname]
	(LocalStorage. dbname))



















