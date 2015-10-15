docker build -t tilosradio/backend .
docker tag tilosradio/backend localhost:5000/tilosradio/backend
docker push localhost:5000/tilosradio/backend
