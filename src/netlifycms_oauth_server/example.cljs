(ns netlifycms-oauth-server.example
  (:require
    ["firebase-functions" :as functions]))


(def example
  #js{:example
      (.. functions
          (runWith #js {:timeoutSeconds 10 :memory "128MB"})
          (region "asia-northeast1")
          -https
          (onRequest (fn handle-request
                       [^js _req, ^js res]
                       (.send res "Hello, World"))))})
