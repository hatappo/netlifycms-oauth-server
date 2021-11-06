(ns netlifycms-oauth-server.core
  (:require
    ["express" :as express]))


(defonce server (atom nil))


(defn routes
  [^js app]
  (.get app "/" (fn [_req res] (.send res "こんにちは、世界！"))))


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
  [& args]
  (start!))
