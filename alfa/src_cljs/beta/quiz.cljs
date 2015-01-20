(ns beta.quiz
	(:require [common.stm :as stm]
						[reagent.core :as re :refer [atom render-component]]
						[reagent.cursor :refer [cur]]
						[beta.comp :as bcomp]
						[common.util :refer [selid animate]]))

(defn generate-soal
	[app]
	(let [lists (cur app [:current-drill])
				n (count lists)]
		(loop [res []]
			(if (= 5 (count res))
				{:answer (nth (rand-int 5) res)
				 :choices (map lists res)}
				(let [random (rand-int n)]
					(recur (if (some #{random} res)
									 res
									 (conj res random))))))))

(defn check-answer
	[app answer soal]
	(if (= answer soal)
		(do (stm/update-user-score! app true)
				{:message (str "You answered correctly, your score is now "
											 (stm/get-user-score app))
				 :answer answer})
		(do (stm/update-user-score! app false)
				{:message "Waduh salah boy!"
				 :answer  soal})))




