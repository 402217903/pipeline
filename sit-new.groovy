def git_tag = 0
build_list = [
'WorkPackage':'FaceHand.Salary.Api,FaceHand.Salary.WebUI,FaceHand.Exam.Api,FaceHand.Exam.WebUI,FaceHand.Birthday.WebUI,FaceHand.Birthday.Api,FaceHand.Weibo.Api,',
'Report':'FaceHand.Report.WebUI,FaceHand.Report.Api,',
'Mall':'FaceHand.Mall.WebApi,FaceHand.Mall.WebUI,',
'smartorder':'FaceHand.SmartOrder.WebUI,FaceHand.SmartOrder.WebAPI,CorpPlatformWeb4,',
'OpenRelation':'FaceHand.OpenRelation.Api,OpenRelation2,',
'Workflow':'FaceHand.Workflow.Api,FaceHand.Workflow.WebUI,',
'CommonApi':'FaceHand.Product.WebApi,FaceHand.Product.WebUI,FaceHand.Comment.WebApi,FaceHand.Comment.WebUI,LogServices,IMServices,CommentServices,LangServices,ShortUrlServices,MaintainServices,NotifyServices,FaceHand.Annex.Api,FaceHand.Annex.Ui,IMServices3,FaceHand.SMS.UI,FaceHand.SMS.Api,',
'Vcard':'FaceHand.Vcard.WebUI,FaceHand.Vcard.Api,',
'Member':'FaceHand.Members.WebUI2,FaceHand.Members.WebApi,',
'Transaction':'FaceHand.Transaction.WebApi,FaceHand.Transaction.WebUI,',
'CRM':'FaceHand.CRM.WebUI,FaceHand.CRM.ImportExportApi,FaceHand.CRM.Api,FaceHand.CustomFields.Api,',
'WebApi':'WebApi,',
'kingdee':'FaceHand.Kingdee.Api,',
'Payment':'FaceHand.Payment.WebUI,FaceHand.Payment.Api,',
'market':'facehand.platform.marketingapi,',
'Reward':'FaceHand.Reward.Api,',
'mpweixin':'FaceHand.mpweixin.Api,FaceHand.mpweixin.Ui,',
'WxLogin':'FaceHand.WxLogin.WebUI,',
'erpjoint':'FaceHand.ErpJoint.Api,',
'QyApp':'FaceHand.Web.QY.App,',
'PCWeb': 'CommunityWeb,PassportWeb,PersonalPlatformWeb,AppFront,',
'CorpPlatformWeb4':'CorpPlatformWeb4,',
'50':'AnnexServices,ImportExportServices,FaceHand.mpweixin.Api,FaceHand.mpweixin.Ui,FaceHand.Web.QY.AppSuiteEvent,FaceHand.Web.QY.AppEvent,BusinessPlatformWeb,WebSite3,',
'CommonWeb':'CommonWeb,Common.Files,CommonPage,Common.TimerServices,',
'Product':'FaceHand.Product.WebApi,FaceHand.Product.WebUI,'
]
def project_build_list = []

echo "${deploy_env}"

node("windows_online_build"){
//   powershell "mkdir D:\\jenkins\\workspace\\sit_new_test\\${deploy_env}\\"
//   powershell "mkdir D:\\jenkins\\deploy\\${deploy_env}\\"
	dir("../sit_new_test/$deploy_env"){
		stage("CheckOut"){
			def a="$Branch"
			def b=a.split('origin/')[1]
			git branch: "$b", credentialsId: '957ca79a-205d-4e70-816f-d09d716fc699', url: 'http://gitlab.facehand.cn/root/zztx_solution.git'
		}
		if ("$build_all" == "true") {
			project_list = ["WorkPackage","Report","Mall","smartorder","OpenRelation","Workflow","Vcard","Member","Transaction",
			"CRM","50","WebApi","kingdee","Payment","market","Reward","mpweixin",
			"WxLogin","erpjoint","QyApp","PCWeb","CorpPlatformWeb4","CommonApi","CommonWeb"]
			for (i in project_list){
				project_build_list.add("${build_list[(i)]}")
			}
		}
		else{
			project_list = "$project".split(',')
			echo "$project_list"
			for (i in project_list){
				project_build_list.add("${build_list[(i)]}")
			}
		}
		echo "$project_build_list"
		def project_build_tmp = ""
		for (i in project_build_list){
			project_build_tmp = project_build_tmp + "$i"
		}
		project_build = project_build_tmp.substring(0,project_build_tmp.length()-1)
		echo "最终编译项目参数：$project_build"
		
		
getDatabaseConnection(type: 'GLOBAL') {
    sql(sql: "use Jenkins;")
    def res_version = sql(sql: "SELECT version_res  FROM  verinfo ORDER BY Id DESC LIMIT 1;")
    println "release verison is : ${res_version}"
    def restemp = res_version[0]
    echo "$restemp"
    def res_tag = restemp.version_res
    echo "$res_tag"
    
    def fix_version = sql(sql: "SELECT version_fix  FROM  verinfo ORDER BY Id DESC LIMIT 1;")
    println "fix verison is : ${fix_version}"
    def fixtemp = fix_version[0]
    echo "$fixtemp"
    def fix_tag = fixtemp.version_fix
    echo "$fix_tag"
    
    def rc_version = sql(sql: "SELECT version_rc  FROM  verinfo ORDER BY Id DESC LIMIT 1;")
    println "rc verison is : ${rc_version}"
    def rctemp = rc_version[0]
    echo "$rctemp"
    def rc_tag = rctemp.version_rc
    echo "$rc_tag"
    
    
    res_versiontag = res_tag.toInteger()
    fix_versiontag = fix_tag.toInteger()
    rc_versiontag = rc_tag.toInteger()
    if ("$closing" == "true"){
        res_versiontag = res_tag.toInteger() + 1
        sql(sql: "INSERT INTO verinfo(Id,version_res,version_rc,version_fix, version_title, DataUpdateTime) VALUES(DEFAULT, ${res_versiontag},0,0,DEFAULT,NOW());")
    }
    if ("$fix_bug" == "true"){
        rc_versiontag = rc_tag.toInteger()
        fix_versiontag = fix_tag.toInteger() + 1
        sql(sql: "INSERT INTO verinfo(Id,version_res,version_rc,version_fix, version_title, DataUpdateTime) VALUES(DEFAULT, ${res_versiontag},${rc_versiontag},${fix_versiontag},DEFAULT,NOW());")
    }
    echo "rc versiontag : $rc_tag"
    rc_version = sql(sql: "SELECT version_rc  FROM  verinfo ORDER BY Id DESC LIMIT 1;")
    println "rc verison is : ${rc_version}"
    rctemp = rc_version[0]
    echo "$rctemp"
    rc_tag = rctemp.version_rc
    echo "$rc_tag"
    rc_versiontag = rc_tag.toInteger() + 1  
    echo "rc tag : $rc_versiontag"
    fix_version = sql(sql: "SELECT version_fix  FROM  verinfo ORDER BY Id DESC LIMIT 1;")
    println "fix verison is : ${fix_version}"
    fixtemp = fix_version[0]
    fix_tag = fixtemp.version_fix
    fix_versiontag = fix_tag.toInteger()
    sql(sql: "INSERT INTO verinfo(Id,version_res,version_rc,version_fix, version_title, DataUpdateTime) VALUES(DEFAULT, ${res_versiontag},${rc_versiontag},${fix_versiontag},DEFAULT,NOW());")
    }
    
    full_tag = "v${res_versiontag}-rc${rc_versiontag}"
    if ("$closing" == "true"){
        full_tag = "v${res_versiontag}"
    }
    if ("$fix_bug" == "true"){
        full_tag = "v${res_versiontag}-fix${fix_versiontag}"
    }
    echo "full_tag = $full_tag"

		
	
		stage('compile'){
			def a="$Branch"
			def b=a.split('origin/')[1]
			echo "project_build = $project_build,b= $b,full_tag = $full_tag"

			withEnv(['project_build = $project_build','b= $b','full_tag = $full_tag']){
				powershell encoding: 'UTF-8', label: '编译项目', returnStatus: true, script: """cd D:\\jenkins\\workspace\\sit_new_test\\${deploy_env}\\build| D:\\sit_new\\build.ps1 '$project_build' 'd:\\deploy\\${deploy_env}' '$b' '$full_tag'  """
			}
		}
	}
    
}



// node('salt-master-test'){
//     def branch_host=['dev_crm':'dev_crm..cn','sit_dh3':'sit_dh3.facehand.cn','sit_crm2':"sit_crm2.facehand.cn","sit_ls":"sit_ls.facehand.cn","sit_dh":"sit_dh.facehand.cn","sit_ls2":"sit_ls2.facehand.cn","50":"pro.facehand.cn","sit_erp":"sit_erp.facehand.cn"]
//     def b="$deploy_env"
//     stage('deploy'){
//         echo "${branch_host[(b)]}"
//         echo "$full_tag"
        
//       // sh label: 'StopAllSite', returnStatus: false,script: "ansible ${branch_host[(b)]} -m script -a '/srv/salt/scripts/stopallsite.ps1'"
//         sh label: '下发代码', returnStatus: false,script: "ansible ${branch_host[(b)]} -m script -a 'chdir=c:/ZZTXData/WebRoot /srv/salt/scripts/git_pull.ps1' "
//         sh label: '切换tag', returnStatus: false,script: "ansible ${branch_host[(b)]} -m win_shell -a 'chdir=c:/ZZTXData/WebRoot git checkout -f ${full_tag}'"
//       // sh label: 'StartAllSite', returnStatus: false,script: "ansible ${branch_host[(b)]} -m script -a '/srv/salt/scripts/startallsite.ps1'"
 
//         sh label: '刷新资源池', returnStatus: false,script: "ansible ${branch_host[(b)]} -m script -a '/srv/salt/scripts/recycleapppool.ps1'"
//       // sh label: '修改静态资源文件', script: "salt ${branch_host[(b)]} cmd.run 'C:\\salt\\bin\\python E:\\tools\\modify_appsetting.py' cwd='E:\\tools'"  

//     }

// }
