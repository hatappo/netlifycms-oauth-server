(ns netlifycms-oauth-server.example
  (:require
    ["firebase-functions" :as functions]))


(def example
  #js{:example
      (-> functions
          (.region "asia-northeast1")
          .-https
          (.onRequest (fn handle-request
                        [^js _req, ^js res]
                        (.send res "こんにちは、世界"))))})
