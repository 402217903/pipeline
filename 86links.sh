 
       def git_addr = "http://192.168.0.113:9999/java/p86links.git"
       def a="$Branch"
       def b=a.split("/")[1]
       
        
  node("k8s-master"){

        stage('获取代码') {
            
            
                echo "start fetch code from git:${git_addr} branch:$a"
                sh "pwd"
                echo "$a"
                git branch:"$b", credentialsId: '4f28bf2c-3842-4fef-b99f-96812bcc24a5', url: "${git_addr}"  
        }



        stage('build') {
              dir("./$service_name"){

                //sh "source /etc/profile" 
                sh "pwd"       
                echo "build"
                sh(script:"gradle clean bootJar -x test")
              }
    
        }
    
  }
