docker build -t tilosradio/backend .
docker tag -f tilosradio/backend tilos.hu:5555/tilosradio/backend
docker push tilos.hu:5555/tilosradio/backend
