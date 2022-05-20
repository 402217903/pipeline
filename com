node('midays'){
    stage('Checkout'){
        git branch: "$Branch", credentialsId: 'a2487a2d-ab7d-43e8-9d31-4318129d2808', url: 'http://gitlab.facehand.cn/root/zztx_solution.git'
    }
    def typelist="$type".split(',')
    def emptytype="$type".size()
    // Compiledatacenter;
    def msbuild_project="Compilelogic;Compilecrm;Compilecrmoa;Compilevcard;Compileretailmall;Compileplatformapp,Compilesmartorder"
    def msbuild_file="E:\\jenkins\\script\\com_pipline\\pipline_new.msbuild"
    // if("$typelist".contains("DLL") || "$emptytype"=="0"){
    //     stage('Compile'){
    //         bat label: '编译脚本', returnStdout: false, script: "cmd.exe /C \"E:\\Program Files (x86)\\Microsoft Visual Studio\\2019\\Professional\\MSBuild\\Current\\Bin\\MSBuild.exe\" /t:$msbuild_project $msbuild_file  && exit %%ERRORLEVEL%%"
    //     }
    // }
    
      if("$typelist".contains("DLL") || "$emptytype"=="0"){
        stage('compile'){
            bat label: '全量编译脚本', returnStdout: false, script: " cmd.exe /C \" \"E:\\Program Files (x86)\\Microsoft Visual Studio\\2019\\Professional\\MSBuild\\Current\\Bin\\MSBuild.exe\" /t:${msbuild_project} ${msbuild_file} \" && exit %%ERRORLEVEL%%"
        }
      }
      
    stage('Archive'){
        //powershell label: '订货plugins_sql拷贝', returnStdout: true, script: 'E:\\jenkins\\script\\com_pipline\\copy_plugins_sql.ps1'
        bat label: '静态资源整合', returnStdout: true, script: 'C:\\salt\\bin\\python.exe E:\\jenkins\\script\\com_pipline\\combineres.py'
        powershell label: '清理目录', returnStdout: true, script: 'E:\\jenkins\\script\\com_pipline\\clear.ps1'
        powershell label: '拷贝代码/静态资源', returnStdout: true, script: 'E:\\jenkins\\script\\com_pipline\\copy_webroot_static.ps1'
        if("$typelist".contains("DLL") || "$typelist".contains("CSHTML")){
            bat label: '打包代码', returnStdout: true, script: '7z a E:\\jenkins\\workspace\\com_pipline\\archive.zip E:\\jenkins\\workspace\\com_pipline\\WebRoot\\'
        }
      if("$typelist".contains("JS") || "$typelist".contains("CSS") ){
          bat label: '打包静态资源', returnStdout: true, script: '7z a E:\\jenkins\\workspace\\com_pipline\\archive.zip E:\\jenkins\\workspace\\com_pipline\\out_static\\'
      }
      if("$emptytype"=="0"){
          bat label: '打包代码', returnStdout: true, script: '7z a E:\\jenkins\\workspace\\com_pipline\\archive.zip E:\\jenkins\\workspace\\com_pipline\\WebRoot\\'
          bat label: '打包静态资源', returnStdout: true, script: '7z a E:\\jenkins\\workspace\\com_pipline\\archive.zip E:\\jenkins\\workspace\\com_pipline\\out_static\\'
      }
      if("$config_publish" == "true"){
          bat label: '打包配置文件', returnStdout: true, script: '7z a E:\\jenkins\\workspace\\com_pipline\\archive.zip E:\\jenkins\\workspace\\com_pipline\\conf\\'
      }
        
    }
}

node('master'){
  stage('Compress'){
      def archiveexist = fileExists '/data/cache/minions/by.handday.com/files/jenkins/workspace/com_pipline/archive.zip'
     
      if ("$archiveexist" == "true"){
          sh label: '清理上传目录', script: 'rm -rf /data/cache/minions/by.handday.com/files/jenkins/workspace/com_pipline/archive.zip'
      }
      sh label: '上传打包文件', returnStdout: true, script: 'salt by.handday.com cp.push \'E:\\jenkins\\workspace\\com_pipline\\archive.zip\''
      sh label: '清空工作目录', script: 'rm -rf /srv/salt/deploy/com_pipline/*'
      sh label: '解压包到工作目录', returnStdout: true, script: ' unzip -o -O GBK -d /srv/salt/deploy/com_pipline/ /data/cache/minions/by.handday.com/files/jenkins/workspace/com_pipline/archive.zip '
      def staticexist=fileExists '/srv/salt/deploy/com_pipline/out_static'
      def webrootexist=fileExists '/srv/salt/deploy/com_pipline/WebRoot'
      if ("$staticexist" == "true"){
            sh label: '压缩静态资源', returnStdout: true, script: 'python /srv/salt/script/com_pipline/compressor.py'
            
            if ("$webrootexist" == "true"){
                sh label: '', returnStdout: true, script: '\\cp -rf /srv/salt/deploy/com_pipline/out_static/* /srv/salt/deploy/com_pipline/WebRoot/'
            }
            
      }
      if ("$webrootexist" == "true"){
        sh label: '处理订货特殊dll文件', returnStdout: true, script: 'python /srv/salt/script/com_pipline/dc_E.py'
      }
  }
  def staticexist=fileExists '/srv/salt/deploy/com_pipline/out_static'
  if("$staticexist" == "true") {
      stage('Cdn'){
          sh label: '处理静态资源目录', returnStdout: true, script: 'python /srv/salt/script/com_pipline/tocdn.py'
          sh label: '压缩静态资源文件', returnStdout: true, script: '''cd /srv/salt/deploy/com_pipline/cdn
                              zip  -r /srv/salt/deploy/com_pipline/cdn.zip ./*'''
          sh label: '下发静态资源包', returnStdout: true, script: 'salt resource_vpc cp.get_file salt://deploy/com_pipline/cdn.zip \'C:\\ZZTXData\\StaticRes\\v2\\cdn.zip\''      
          sh label: '解压资源文件', returnStdout: true, script: 'salt resource_vpc cmd.script salt://script/com_pipline/unzip.ps1 shell=\'powershell\' '
          sh label: '上传cdn', returnStdout: true, script: 'salt resource_vpc cmd.run \'java -cp ".;C:\\ZZTXService\\cos_sync_tools-master\\dep\\*;C:\\ZZTXService\\cos_sync_tools-master\\src\\main\\resources\\*" com.qcloud.cos.cos_sync.main.CosSyncMain\'  cwd=\'C:\\ZZTXService\\cos_sync_tools-master\''
        }
  }

  stage('Deploy'){
      def webrootexit= fileExists '/srv/salt/deploy/com_pipline/WebRoot'
      if ("$webrootexit" == "true"){
          sh label: '拷贝代码文件到git目录', returnStdout: true, script: '\\cp -rf /srv/salt/deploy/com_pipline/WebRoot/* /data/build/WebRoot/'
          sh label: '处理配置文件', returnStdout: true, script: 'python /srv/salt/script/com_pipline/releaseconfig.py'
      }
      def confexist= fileExists '/srv/salt/deploy/com_pipline/conf'
      if ("$confexist" == "true"){
          sh label: '拷贝配置文件到git目录', returnStdout: true, script: '\\cp -rf /srv/salt/deploy/com_pipline/conf/* /data/build/conf/'
      }
      
      sh label: '修改静态资源版本', returnStdout: true, script: 'python /srv/salt/script/com_pipline/modify_setting.py'
      sh label: 'git操作', returnStdout: true, script: 'python /srv/salt/script/com_pipline/git.py'
      if ("$webrootexit" == "true"){
        sh label: '停止zztx_erp服务', returnStdout: true, script: 'salt -N ERP cmd.script salt://script/zztx_service.ps1 shell=\'powershell\''
      
 //       sh label: '停止资源服务中的serversfwk服务', returnStdout: true, script: 'salt resource_vpc cmd.script salt://script/stop_serversfwk.ps1 shell=\'powershell\''
      }
      sh label: '下发代码文件', returnStdout: true, script: '''salt -N ALL cmd.run \'git fetch --all\' cwd=\'C:\\ZZTXData\'
                                  salt -N ALL cmd.run \'git reset --hard origin/master\' cwd=\'C:\\ZZTXData\'
                                  salt -N ALL cmd.run \'git pull origin master\' cwd=\'C:\\ZZTXData\''''
    //   if ("$webrootexit" == "true"){
    //     // sh label: '启动zztx_erp服务', returnStdout: true, script: 'salt -N ERP cmd.script salt://script/start_zztx_server.ps1 shell=\'powershell\''
      
    //     //sh label: '启动资源服务中的serversfwk服务', returnStdout: true, script: 'salt resource_vpc cmd.script salt://script/start_serversfwk.ps1 shell=\'powershell\''     
    //   } 
    }
     
 }