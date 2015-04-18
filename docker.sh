docker build -t tilos/frontend .
docker tag tilos/frontend localhost:5000/tilos/frontend
docker push localhost:5000/tilos/frontend