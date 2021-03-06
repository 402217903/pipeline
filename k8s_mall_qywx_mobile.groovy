def project_tag=[:]
def project_list=[
"mall-qywx-mobile":"mall-qywx-mobile"]
def projectsrc_list=[
"mall-qywx-mobile":"http://gitlab.facehand.cn/static/mall_qywx_mobile.git"]
def projects_list="$project".split(',')
def projectsrcs=[]


def image_tag=[:]
projects_list.each{
    projectsrcs.add("${project_list[(it)]}")
}

node("docker_build"){
    projectsrcs.unique().each{
        if ("${ fileExists it }"=="false"){
            sh label: '', script: "mkdir -p $it"
        }

        def project_base_name="$it/"
        
        dir("$it"){
            def a="$Branch"
            def b=a.split('origin/')[1]
            echo "$b"
            git branch: "$b", credentialsId: '957ca79a-205d-4e70-816f-d09d716fc699', url: "${projectsrc_list[(it)]}"
            // git branch:"release", credentialsId: '957ca79a-205d-4e70-816f-d09d716fc699', url: "${projectsrc_list[(it)]}"
            def project_dockerfile=sh(script: "/opt/go/zztx_cli/main find --src /data/jenkins/workspace/k8s_mall_qywx_mobile/$it/  --fn Dockerfile", returnStdout:true).trim()
            def project_dockerfiles="$project_dockerfile".split('\n')
            echo "==========="
            echo "$projects_list"
            projects_list.each{
                def project_name="$it" 
                // project_dockerfiles.each{

                    if ("$it".contains("$project_name")){
                        def new_project_name="$project_name".toLowerCase()
                        // def dockerfile_path= "${it.split(project_base_name)[1]}"
                        echo "xxxxxxxxxxxxxxxxxxxxxxxx   $project_base_name"

                        //处理version迭代版本
                        if ("${fileExists 'cur_version.txt'}" == "false"){
                            sh(script: "echo '$new_project_name:v1'>cur_version.txt",returnStdout:true)
                        }

                        if ("${fileExists 'cur_tag.txt'}"=="false"){
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
                        
                        if("$new_project_name" == "mall-qywx-mobile") {
                             if ("$isinstall" == "true"){
                             sh(script:"npm cache clean --force")
                             sh(script:"rm -rf node_modules package-lock.json")
                             sh(script: "npm install")
                             }
                         //sh(script:"npm run test")
                        sh(script:"npm run build")
                        }



                        image_tag["$new_project_name"]="$full_tag"
                        project_tag."${new_project_name}"="$full_tag"
                        sh(script: """
                        docker build -f Dockerfile -t handday.tencentcloudcr.com/handday/$new_project_name:$full_tag .""")           
                        sh(script:"docker push handday.tencentcloudcr.com/handday/$new_project_name:$full_tag")
                        sh(script:"git tag $full_tag")
                        sh(script:"git push --tags")
                        // withCredentials([usernamePassword(credentialsId:"957ca79a-205d-4e70-816f-d09d716fc699",
                        //                                 usernameVariable:"GIT_USERNAME",
                        //                                 passwordVariable: "GIT_PASSWORD")]){
                        // sh(script:"git config --local credential.helper '!p() {echo username=\\$GIT_USERNAME; echo password=\\$GIT_PASSWORD;}; p'")
                        // sh(script:"git config --global user.name 'root' ")
                        // sh(script:"git config --global user.email 'admin@example.com' ")
                        // sh(script:"git push --tags")
                        // }
                        
                       
                    }
                // }
            }
           
        }
        
    }
    // dir("../../helm/"){
    //         projects_list.each{
    //             def project_name="$it".toLowerCase()
    //             sh label: '修改helm版本，大版本加1', script: "project_name=${project_name};"+
    //             '''
    //                 helm_version=$(sed -n \'/version: /p\' $project_name/Chart.yaml | awk -F \':\' \'{print $2}\'|sed \'s/ //g\');
    //                 helm_min_version=$(echo $helm_version |awk -F \'.\' \'{print $3}\');
    //                 let \'helm_min_version=helm_min_version+1\';
    //                 new_helm_version=$(echo $helm_version|awk -F \'.\' \'{print $1"."$2".""\'$helm_min_version\'"}\');
    //                 sed -i \"s/version: $helm_version/version: $new_helm_version/g\" $project_name/Chart.yaml'''
    //             sh(script:"helm package $project_name")
    //             sh(script:
    //             "project_name=${project_name};"+
    //             '''
    //               helm_version=$(sed -n \'/version: /p\' $project_name/Chart.yaml | awk -F \':\' \'{print $2}\'|sed \'s/ //g');
    //               chart_name=$project_name"-"$helm_version".tgz";
    //               helm push $chart_name handday
    //             ''')
    //         }
    //     }
    
}
node("k8s-master-test"){
    sh "pwd"
    dir("$zztx_hostenv/"){
        sh "pwd"
        projects_list.each{
        
            def project_name="${it}".toLowerCase()
            echo "$image_tag  $project_name" 

            def tag="${image_tag[(project_name)]}" 
            
            sh(script:"helm repo update")
            sh(script:
                "project_name=${project_name};"+
                "config_name=$project_name\".values.yaml\";"+
                "image_tag=${tag};"+
                '''helm_chart_name=$(echo $project_name| awk -F \'.\' \'BEGIN{a=""}{i=1;i<NF}{a=a$i"-";i++}{print a$NF}\' );
                    new_helm_chart_name=$helm_chart_name"-"$zztx_hostenv
                if [  -n "$(helm list | grep $project_name)" ]; then                   
                    helm upgrade --set image.tag=$image_tag  ${new_helm_chart_name}  handday-${zztx_hostenv}/$project_name       
                else
                    helm install --set image.tag=$image_tag  ${new_helm_chart_name}  handday-${zztx_hostenv}/$project_name
                fi
                '''
                )
        }

    }
    
}
