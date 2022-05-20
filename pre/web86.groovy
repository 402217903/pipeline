 
       def git_addr = "http://192.168.0.113:9999/frontend/js-ms.git"
       def a="$Branch"
       def b=a.split('origin/')[1]
       
    //   def b=a.split("/")[1]+'/'+ a.split("/")[-1]
        
        
  node("k8s-master"){

        stage('获取代码') {
            
                echo "start fetch code from git:${git_addr} branch:$a"
                sh "pwd"
                echo "$b"
                
                // git branch:'$a', credentialsId: '4f28bf2c-3842-4fef-b99f-96812bcc24a5', url: "${git_addr}"  
                deleteDir()
                git branch:"$b", credentialsId: 'b36dd50e-1783-4c8b-86a8-e627f74fe3d2', url: "${git_addr}"
            
        }

        stage('版本处理') {
                getDatabaseConnection(type: 'GLOBAL') {
							sql(sql: "use devops;")
							def res_version = sql(sql: "SELECT version_res  FROM  grpcverinfo where project_name='$service_name' ORDER BY Id DESC LIMIT 1;")
							println "release verison is : ${res_version}"
							def restemp = res_version[0]
							echo "$restemp"
							def res_tag = restemp.version_res
							echo "$res_tag"

							def fix_version = sql(sql: "SELECT version_fix  FROM  grpcverinfo where project_name='$service_name' ORDER BY Id DESC LIMIT 1;")
							println "fix verison is : ${fix_version}"
							def fixtemp = fix_version[0]
							echo "$fixtemp"
							def fix_tag = fixtemp.version_fix
							echo "$fix_tag"

							def rc_version = sql(sql: "SELECT version_rc  FROM  grpcverinfo where project_name='$service_name' ORDER BY Id DESC LIMIT 1;")
							println "rc verison is : ${rc_version}"
							def rctemp = rc_version[0]
							echo "$rctemp"
							def rc_tag = rctemp.version_rc
							echo "$rc_tag"


							res_versiontag = res_tag.toInteger()
							fix_versiontag = fix_tag.toInteger()
							rc_versiontag = rc_tag.toInteger()
							
							res_versiontag = res_tag.toInteger() + 1
								sql(sql: "INSERT INTO grpcverinfo(Id,project_name,version_res,version_rc,version_fix, version_title, DataUpdateTime) VALUES(DEFAULT, '${service_name}',${res_versiontag},0,0,DEFAULT,NOW());")
							
							
							rc_versiontag = rc_tag.toInteger()
							fix_versiontag = fix_tag.toInteger() + 1
							sql(sql: "INSERT INTO grpcverinfo(Id,project_name,version_res,version_rc,version_fix, version_title, DataUpdateTime) VALUES(DEFAULT, '${service_name}',${res_versiontag},${rc_versiontag},${fix_versiontag},DEFAULT,NOW());")
							

							echo "==========================================="
							echo "rc versiontag : $rc_tag"
							rc_version = sql(sql: "SELECT version_rc  FROM  grpcverinfo where project_name='$service_name' ORDER BY Id DESC LIMIT 1;")
							println "rc verison is : ${rc_version}"
							rctemp = rc_version[0]
							echo "$rctemp"
							rc_tag = rctemp.version_rc
							echo "$rc_tag"
							rc_versiontag = rc_tag.toInteger() + 1  
							echo "rc tag : $rc_versiontag"
							fix_version = sql(sql: "SELECT version_fix  FROM  grpcverinfo where project_name='$service_name' ORDER BY Id DESC LIMIT 1;")
							println "fix verison is : ${fix_version}"
							fixtemp = fix_version[0]
							fix_tag = fixtemp.version_fix
							fix_versiontag = fix_tag.toInteger()
							echo "------------------------------------------------"
							echo "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
							echo "${service_name}"
							sql(sql: "INSERT INTO grpcverinfo(Id,project_name, version_res,version_rc,version_fix, version_title, DataUpdateTime) VALUES(DEFAULT, '${service_name}',${res_versiontag},${rc_versiontag},${fix_versiontag},DEFAULT,NOW());")
						
							
						full_tag = "v${res_versiontag}-rc${rc_versiontag}"
						
						echo "full_tag = $full_tag"
                }





        }

          stage('build') {
                //sh "source /etc/profile" 
                sh "pwd"
                // sh "ls"
                echo "build"
                sh "npm install -g yarn" 
                echo "执行install..."
                sh "source /etc/profile"
                sh "yarn install" 
                echo "执行build..."
                sh "npm run build:prod"
              
        dir("/home/jenkins/workspace/webbuild/$service_name"){ 
                sh(script:
                '''if [ -e dist ];then
                        rm -rf dist
                        cp -r /home/jenkins/workspace/web/dist ./
                 else
                        cp -r /home/jenkins/workspace/web/dist ./
                  fi    
                '''
                )
                sh "pwd"
            stage('docker编译') {
                sh "pwd"
                sh(script: """
                docker build -f Dockerfile -t 192.168.0.189/library/$service_name:$full_tag .""")
                sh "docker login -u admin -p Harbor12345 192.168.0.189"
                sh (script: "docker push 192.168.0.189/library/$service_name:$full_tag")
            }
          }
      }
        stage('k8s部署'){
                dir("/home/jenkins/workspace/helm"){

                sh "pwd"
                echo "$full_tag"

              sh(script: 

                """
                if [  -n "\$(helm list | grep ${service_name})" ]; then
                    helm upgrade  --set image.tag=${full_tag}  ${service_name}  ${service_name}/
    
                else
                    helm install  --set image.tag=${full_tag}  ${service_name}  ${service_name}/
                fi
                """
                )

                }
            }
            
         

    
  }
