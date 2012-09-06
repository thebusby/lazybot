; Written by TheBusby <thebusby@gmail.com>
; Licensed under the EPL
(ns lazybot.plugins.yammer
  (:use [lazybot registry info]
        [clojure.data.json :only [read-json]]
        [somnium.congomongo :only [fetch fetch-one insert! destroy!]])
  (:require [clj-http.client :as http]
            [oauth.client :as oauth]))


;; Create a Consumer, in this case one to access Twitter.
;; Register an application at Twitter (http://twitter.com/oauth_clients/new)
;; to obtain a Consumer token and token secret.
(defonce consumer (oauth/make-consumer "uxJaP5g7ijF1NTLmbeorsg"
                                       "j4a9c4jKEMu6EapPrxMteVSY23KlBprho923DXx9jg"
                                       "https://www.yammer.com/oauth/request_token"
                                       "https://www.yammer.com/oauth/access_token"
                                       "https://www.yammer.com/oauth/authorize"
                                       :hmac-sha1))

;; Fetch a request token that a OAuth User may authorize
;;
;; If you are using OAuth with a desktop application, a callback URI
;; is not required.

; (defonce request-token (oauth/request-token consumer))

;; Send the User to this URI for authorization, they will be able
;; to choose the level of access to grant the application and will
;; then be redirected to the callback URI provided with the
;; request-token.

; (def user-go-uri (oauth/user-approval-uri consumer (:oauth_token request-token)))

;; Assuming the User has approved the request token, trade it for an access token.
;; The access token will then be used when accessing protected resources for the User.
;;
;; If the OAuth Service Provider provides a verifier, it should be included in the
;; request for the access token.  See [Section 6.2.3](http://oauth.net/core/1.0a#rfc.section.6.2.3) of the OAuth specification
;; for more information.
;; (def access-token-response (oauth/access-token consumer
;;                                                request-token
;;                                                "B9DD"))

; (def access-token-response {:oauth_token "f6AIdcegSo4T8GsFc3mFDg"
;                             :oauth_token_secret "BpboH2fBWQPmpNoFByNLj6XCrXl3LGQkLk2enNQg0pU"})

;; Each request to a protected resource must be signed individually.  The
;; credentials are returned as a map of all OAuth parameters that must be
;; included with the request as either query parameters or in an
;; Authorization HTTP header.
;; (def credentials (oauth/credentials consumer
;;                                     (:oauth_token access-token-response)
;;                                     (:oauth_token_secret access-token-response)
;;                                     :GET
;;                                     "https://www.yammer.com/api/v1/messages.json"
;;                                     {:status "posting from Yammer Plug-in from LazyBot"}))


;; ;; Post with clj-apache-http...
;; (def bar (http/get "https://www.yammer.com/api/v1/messages.json"
;;                    {:query-params (merge credentials
;;                                          {:status "posting from #clojure with #oauth"})
;;                     }))



(defn get-creds-messages
  ([creds] (get-new-tweets username nil))
  ([creds last-message-id]
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




;; (def group-message-query
;;  "https://www.yammer.com/api/v1/messages/in_group/[:id].json")

;; (def group-user-liked-equery
;;   "https://www.yammer.com/api/v1/messages/liked_by/[:id].json")








(comment

  (def pp clojure.pprint/pprint)



  (str "newer_than=") ; id
  (str "threaded=true")

"https://www.yammer.com/oauth/authorize?oauth_token=REQUEST_TOKEN"






  )