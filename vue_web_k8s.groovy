
def projects_list="$project".split(',')
def image_tag=[:]
def project_src=["smartorder":"http://gitlab.facehand.cn/lm/zztx_dh_web.git","report":"http://gitlab.facehand.cn/lm/zztx_crm_web.git","weibo":"http://gitlab.facehand.cn/lm/zztx_crm_web.git","mall":"http://gitlab.facehand.cn/static/zztx_ls_web.git"]


node('docker_build'){
    stage('git checkout'){
        for (i in projects_list) {
            git branch: "$Branch", credentialsId: '957ca79a-205d-4e70-816f-d09d716fc699', url: "${project_src[(i)]}"
        }
    }
       
    stage('npm '){
        def project_name="$project"
        echo "$project"
        if("$project" == "mall"){
            dir_path = "mini_store_pc"
        }
        else{
            dir_path = "$project"
        }
        
        dir("$dir_path"){
            if("$isInstall"=="true"){
               // sh(script:"rm -rf node_modules package-lock.json")
                sh(script:"npm install")
            }
               if("$isClearn"=="true"){
               sh(script:"rm -rf node_modules package-lock.json")
            }
            if("$isTest" == "true"){
                sh(script:"npm run test")
            } else{
                sh(script:"npm run build")
            }


            def new_project_name="$project".toLowerCase()
           
            //处理version迭代版本
            if ("${fileExists 'cur_version.txt'}" == "false"){
                sh(script: "echo '$new_project_name:v1'>cur_version.txt",returnStdout:true)
            }

          
            if ("${fileExists 'cur_tag.txt'}" == "false"){
                sh(script: "echo '$new_project_name:v1-rc0'>cur_tag.txt",returnStdout:true)
            }

            if ("$closing" == "false"){
                if ("$fix_bug" == "false"){
                    def version_tag=sh(script:"sed -n '/$new_project_name/p' cur_version.txt | awk -F ':v' '{print \$2}'",returnStdout:true)
                    def version=version_tag.toInteger()
                    echo "=========当前版本version======="
                    echo "$version"
        
                    def old_tag=sh(script:"sed -n '/$new_project_name/p' cur_tag.txt | awk -F 'rc' '{print \$2}'",returnStdout:true)
                    tag = old_tag.toInteger()+1
                    echo "==========当前版本tag========="
                    echo "$tag"
                    sh(script: "name=${new_project_name};"+"oldtag=${old_tag}"+"newtag=${tag};"+"version=${version};"+'sed -i "s/$name:v$version-rc$oldtag/$name:v$version-rc$newtag/g" cur_tag.txt')
                    full_tag = "v$version-rc$tag"
                    git_tag = "$full_tag"
                    echo "================未封版full_tag============"
                    echo "$full_tag"
        
        
                }
                else{
                    def version_tag=sh(script:"sed -n '/$new_project_name/p' last_version.txt | awk -F ':v' '{print \$2}'",returnStdout:true)
                    def version=version_tag.toInteger()
                    echo "=========fixbug版本version======="
                    echo "$version"
        
                    def old_tag=sh(script:"sed -n '/$new_project_name/p' last_tag.txt | awk -F 'rc' '{print \$2}'",returnStdout:true)
                    tag = old_tag.toInteger()+1
                    echo "==========fixbug版本tag========="
                    echo "$tag"
                    sh(script: "name=${new_project_name};"+"oldtag=${old_tag}"+"newtag=${tag};"+"version=${version};"+'sed -i "s/$name:v$version-rc$oldtag/$name:v$version-rc$newtag/g" last_tag.txt')
                    if ("${fileExists 'fixbug.txt'}"=="false"){
                        sh(script: "echo '$new_project_name:v$version-fix0' > fixbug.txt",returnStdout:true)
                    }
                    full_tag = "v$version-rc$tag"
                    git_tag = "$full_tag"
                    echo "================未封版full_tag============"
                    echo "$full_tag"
                }
        
            }
            else {
                if ("$fix_bug" == "false"){
                    def version_tag=sh(script:"sed -n '/$new_project_name/p' cur_version.txt | awk -F ':v' '{print \$2}'",returnStdout:true)
                    def version=version_tag.toInteger()
                    full_tag = "v$version"
                    git_tag = "$full_tag"
                    def new_version=version.toInteger()+1
                    echo "======正常封版========"
                    echo "正常封版tag: $full_tag"
                    sh(script: "name=${new_project_name};"+"oldversion=${version};"+"newversion=${new_version};"+'sed -i "s/$name:v$oldversion/$name:v$newversion/g" cur_version.txt')
                    sh(script: "echo '$new_project_name:v$version' > last_version.txt",returnStdout:true)
        
                    def old_tag=sh(script:"sed -n '/$new_project_name/p' cur_tag.txt|awk -F 'rc' '{print \$2}'",returnStdout:true)
                    tag=old_tag.toInteger()+1
                    sh(script: "name=${new_project_name};"+"oldtag=${old_tag}"+"newtag=${tag};"+"version=${version};"+"new_version=${new_version};"+'sed -i "s/$name:v$version-rc$oldtag/$name:v$new_version-rc0/g" cur_tag.txt')
                    sh(script: "echo 'v$version-rc$old_tag' > last_tag.txt ",returnStdout:true)
                }
                else {
                    def version_tag=sh(script:"sed -n '/$new_project_name/p' last_version.txt | awk -F ':v' '{print \$2}'",returnStdout:true)
                    def version=version_tag.toInteger()
                    fix_version = sh(script:"sed -n '/$new_project_name/p' fixbug.txt | awk -F 'fix' '{print \$2}'",returnStdout:true)
                    def new_fix_version = fix_version.toInteger()+1
        
                    sh(script: "name=${new_project_name};"+"old_fix_veresion=${fix_version}"+"new_fix_version=${new_fix_version}"+"version=${version};"+'sed -i "s/$name:v$version-fix$old_fix_veresion/$name:v$version-fix$new_fix_version/g" fixbug.txt')
                    sh(script: "echo '$new_project_name:v$version-fix$new_fix_version' > fixbug.txt",returnStdout:true)
                    full_tag = "v$version-fix$new_fix_version"
                    git_tag = "$full_tag"
                    echo "=======fixbug版本封版=========="
                    echo "fixbug版本封版： $full_tag"
        
                }
        
            }

            image_tag["$new_project_name"]="$full_tag"
            echo "========================="
            echo "$image_tag"
            sh(script: """
                docker build  -t handday.tencentcloudcr.com/handday/$new_project_name:$full_tag .""")
              // withDockerRegistry(credentialsId: '0f265d75-a863-4fab-b5e7-ab99242016f8', url: 'http://harbor.handday.com') {
                    sh(script:"docker push handday.tencentcloudcr.com/handday/$new_project_name:$full_tag")
                    sh(script:"git tag $full_tag")
                    sh(script:"git push --tags")
              // }
            
        }
    
    }
    stage('helm'){
        dir("../../helm/"){  
                sh(script: "project_name=${project};"+
                '''
                    helm_version=$(sed -n \'/version: /p\' $project_name/Chart.yaml | awk -F \':\' \'{print $2}\'|sed \'s/ //g\');
                    helm_min_version=$(echo $helm_version |awk -F \'.\' \'{print $3}\');
                    let \'helm_min_version=helm_min_version+1\';
                    new_helm_version=$(echo $helm_version|awk -F \'.\' \'{print $1"."$2".""\'$helm_min_version\'"}\');
                    sed -i \"s/version: $helm_version/version: $new_helm_version/g\" $project_name/Chart.yaml
                ''')
                sh(script: "helm package $project")
                sh(script:
                "project_name=${project};"+
                '''
                    helm_version=$(sed -n \'/version: /p\' $project_name/Chart.yaml | awk -F \':\' \'{print $2}\'|sed \'s/ //g');
                    chart_name=$project_name"-"$helm_version".tgz";
                    helm push $chart_name handday
                '''
                )   
        }
    }
}
node('k8s-master-test'){
    if("$zztx_hostenv"==""){
        zztx_hostenv="."
    }
    dir("$zztx_hostenv/"){
        sh(script:"helm repo update")
        projects_list.each{
            echo "$it"
            def tag="${image_tag[(it)]}"
            if("$it"=="monitor"){
                zztx_hostenv="test"
            }
            sh(script:"project_name=${it};"+
                "config_name=${it}\".values.yaml\";"+
                "image_tag=${tag};"+
                "chart_name=${it}\"-\"${zztx_hostenv}"+
                '''
                
                if [  -n "$(helm list | grep $chart_name)" ]; then
                    helm upgrade --set image.tag=$image_tag  ${chart_name} -f $config_name handday/$project_name 
                    
                else
                    helm install --set image.tag=$image_tag  ${chart_name} -f $config_name handday/$project_name
                fi
                '''
            
            )
        }
    }
    
}
