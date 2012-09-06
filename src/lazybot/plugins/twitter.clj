; Written by TheBusby <thebusby@gmail.com>
; Licensed under the EPL
(ns lazybot.plugins.twitter
  (:use [lazybot registry info]
        [somnium.congomongo :only [fetch fetch-one insert! destroy!]]
        [twitter.api.restful])
  (:require [clj-http.client :as http]))


(defn get-new-tweets
  ([username] (get-new-tweets username nil))
  ([username last-tweet-id]
     (let [params (merge {:screen-name username
                          :include-rts 1
                          :count 3}
                         (if last-tweet-id
                           {:since-id last-tweet-id}
                           {}))]
       (->> (user-timeline :params params)
            :body
            (map (comp #(zipmap [:text :id :user :rt_user] %)
                       (juxt :text
                             :id
                             (comp :screen_name :user)
                             (comp :screen_name :user :retweeted_status))))
                        (map (fn [{:keys [user rt_user] :as rec}] (assoc rec :poster (or rt_user user))))))))
;; (defplugin
;;   (:hook :on-any
;;          (fn [{:keys [message] :as com-m}]
;;            (when message
;;              (if-let [[url] (re-find #"(?i)(http://www.youtube.com[^\s]*)" message)]
;;                (send-message com-m (str "You linked to " url " didn't you?"))))))
  
;;   (:indexes [[:server :channel :nick]]))


