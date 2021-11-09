(ns netlifycms-oauth-server.core
  (:require
    ["express" :as express]
    ["firebase-functions" :as functions]
    ["simple-oauth2" :as simple-oauth2]))


;; production-env
(def oauth (.. functions config -oauth))


;; local-env
;; (def oauth
;;   (clj->js {:client_id "11111"
;;             :client_secret "22222"}))


(def config
  (clj->js
    {:client {:id ^js/string (. oauth -client_id)
              :secret ^js/string (. oauth -client_secret)}
     :auth  {:tokenHost "https://github.com"
             :tokenPath "/login/oauth/access_token"
             :authorizePath "/login/oauth/authorize"}}))


(defonce client (simple-oauth2/AuthorizationCode. config))


(defn random-str
  [len]
  (apply str (take len (repeatedly #(rand-nth "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890")))))


(defn html
  [res t]
  (str "<!doctype html><html><head></head><body><script>
  (function() {
    function receiveMessage(e) {
      console.log(\"Called receiveMessage: %o\", e)
      window.opener.postMessage(
        'authorization:github:" res ":" (.stringify js/JSON t) "',
        e.origin
      )
      window.removeEventListener(\"message\",receiveMessage,false);
    }
    window.addEventListener(\"message\", receiveMessage, false)
    console.log(\"Sending message to GitHub\")
    window.opener.postMessage(\"authorizing:github\", \"*\")
    })()
  </script></body></html>"))


(defn route-auth
  [_req res]
  (let [url (.authorizeURL client
                           (clj->js
                             {:scope "repo,user"
                              ;; :redirect_uri "http://localhost:3000/callback" ;; local-env
                              :state (random-str 32)}))]
    (.redirect res url)))


(defn route-callback
  [req res]
  (let [options (clj->js {:code (.. req -query -code)})]
    (-> (.getToken client options)
        (.then (fn [^js token]
                 (.send res (html "success" (clj->js {:token (.. token -token -access_token)
                                                      :provider "github"})))))
        (.catch
          #((println "An error occureed to get access token:" %)
            (.send res (html "error" %)))))))


(defn route-simply
  [body]
  (fn [_req res] (.send res body)))


(defn routes
  [^js app]

  ;; production-env
  (.get app "/" (fn [_req res] (.redirect res 301 "/oauth/auth")))
  ;; local-env
  ;; (.get app "/" (fn [_req res] (.redirect res 301 "/auth")))

  (.get app "/auth" route-auth)
  (.get app "/callback" route-callback)
  (.get app "/success" (route-simply "success"))
  (.get app "/health" (route-simply "ok"))
  app)


(defonce server (atom nil))


(defn start-server
  "ルーティングやポートなどを設定"
  []
  (let [app (express)
        prod? (= (.get app "env") "production")
        port (if (nil? (.-PORT (.-env js/process)))
               3000
               (int (.-PORT (.-env js/process))))]
    (when prod? (.set app "trust proxy" 1))
    (.use app "/assets" (.static express "assets"))
    (routes app)
    (.listen app port (fn [] (prn "Listening ...")))))


(defn start!
  "サーバを起動し `server` atom にサーバインスタンスを束縛"
  []
  (reset! server (start-server)))


(defn stop!
  "コネクションを閉じて `server` atom の束縛を解く"
  []
  (.close @server)
  (reset! server nil))


(defn main
  "アプリケーションのエントリポイント"
  [& _args]
  (start!))


(def exports
  #js{:oauth (-> functions (.region "asia-northeast1") .-https (.onRequest (routes (express))))})
