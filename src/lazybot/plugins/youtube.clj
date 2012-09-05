; Written by TheBusby <thebusby@gmail.com>
; Licensed under the EPL
(ns lazybot.plugins.youtube
  (:use [lazybot registry info [utilities :only [bold-str]]]
        [clojure.data.json :only [read-json]]
        [clojure.core.incubator :only [-?>>]]
        [somnium.congomongo :only [fetch fetch-one insert! destroy!]])
  (:require [clj-http.client :as http]))

(defn get-youtube-data [vid]
  (-> (http/get (str "https://gdata.youtube.com/feeds/api/videos/" vid)
                {:query-params {"v" "2" "alt" "jsonc"}})
      :body
      read-json
      :data))

; When a link to a youtube video is seen, this
; prints the video title, view count, and rating
(defplugin
  (:hook :on-any
         (fn [{:keys [message] :as com-m}]
           (if-let [fields (-?>> message
                                 (re-find #"(?i)https?://www.youtube.com/watch\?v=([^\s&]*)")
                                 second
                                 get-youtube-data)]
             (send-message com-m (str "Title: "  (bold-str (:title fields)) "  "
                                      "Views: "  (bold-str (:viewCount fields)) "  "
                                      "Rating: " (bold-str (:rating fields)))))))
  
  (:indexes [[:server :channel :nick]]))

