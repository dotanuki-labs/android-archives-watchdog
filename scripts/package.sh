#! /usr/bin/env bash

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

readonly version=$(grep "latest" src/main/resources/versions.properties | sed "s/latest=//g")
readonly fatjar="build/libs/arw-$version.jar"
readonly output_folder="build/bin"
readonly target_binary="$output_folder/arw"

if ! test -f "$fatjar"; then
  echo "✗ Error : missing target fatjar!"
  echo
  exit 1
fi

rm -rf "$"$output_folder""
mkdir -p "$output_folder"
touch "$output_folder/arw"
echo "#! /usr/bin/env bash" >>"$target_binary"
echo "" >>"$target_binary"
echo "exec java -Xmx1024m -jar \$0 \"\$@\"" >>"$target_binary"
echo "" >>"$target_binary"
echo "" >>"$target_binary"
echo "" >>"$target_binary"

cat "$fatjar" >>"$target_binary"
chmod +x "$target_binary"
