rm -rf target
mkdir -p target
git show -s HEAD > target/version.txt
docker build -t tilosradio/backend .
docker tag -f tilosradio/backend tilos.hu:5555/tilosradio/backend
docker push tilos.hu:5555/tilosradio/backend
rm -rf target
