(ns beta.navigation
  "Managing the navigational pattern for the app"
  (:require [common.stm :as stm]))

(defn set-page!
  "Change the app-state :current-page to the targeted-page"
  [app targeted-page]
  (swap! app assoc :current-page targeted-page))



