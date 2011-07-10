clear
git checkout dev
git status
read

git rebase master
git checkout master
git status
git merge dev
git push origin
git checkout dev

