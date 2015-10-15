docker build -t tilosradio/backend .
docker tag -f tilosradio/backend localhost:5000/tilosradio/backend
docker push localhost:5000/tilosradio/backend
