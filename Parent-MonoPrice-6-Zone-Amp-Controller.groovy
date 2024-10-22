/* 
Parent driver fo Monoprice 6 zone audio 
monoprice.com/product?p_id=10761
This driver is to control the monoprice 6 zone amplifier. 
I wrote this diver for personal use. If you decide to use it, do it at your own risk. 
No guarantee or liability is accepted for damages of any kind. 
for the driver to work it also needs RS232 to Ethernet like this one:
        https://www.aliexpress.com/item/32988953549.html?spm=a2g0o.productlist.0.0.517f5e27r8pql4&algo_pvid=f21f7b9e-0d3b-4920-983c-d9df0da59484&algo_expid=f21f7b9e-0d3b-4920-983c-d9df0da59484-1&btsid=0ab6f83115925263810321337e7408&ws_ab_test=searchweb0_0,searchweb201602_,searchweb201603_
        https://www.amazon.com/USR-TCP232-302-Serial-Ethernet-Converter-Support/dp/B01GPGPEBM/ref=sr_1_6?dchild=1&keywords=RS232+to+Ethernet&qid=1592526464&sr=8-6 or similar 
        is been test it too on:
        https://www.amazon.ca/gp/product/B087J9F6LF/ref=ppx_yo_dt_b_asin_title_o02_s00?ie=UTF8&psc=1
        08/11/2020
        this driver also work on a rasberry pi running ser2net.py by Pavel Revak https://github.com/pavelrevak/ser2tcp
        is recomended to daemonize the scrips instruction on the gihub https://github.com/martinezmp3/Hubitat-Monoprice-6-zone-controller/blob/master/README.md
        08/12/2020 
        compatibility with 2 and 3 amps connected as a chain i dont own a second amp
        08/20/2020 
        Parent save source name to be display on dashboard
Jorge Martinez
*/

metadata {
	definition (name: "MonoPrice 6 Zone Amp Controller - Parent", namespace: "jorge.martinez", author: "Jorge Martinez"){
		capability "Polling"
		capability "Telnet"
		capability "Initialize"
        capability "Health Check"
//		capability "Actuator"
//		capability "Switch"
//      capability "Sensor"
//		capability "AudioVolume"
		command "recreateChildDevices"
    	command "poll"
		command "forcePoll"
		command "sendMsg" , ["STRING"]
		command "CloseTelnet"
		command "setChildzones"
		command "Unschedule"
		command "healthCheck"
        
        attribute  "healthStatus", "enum", [ "unknown", "offline", "online" ]
	}
	preferences {
		section("Device Settings:") 
		{
			input "IP", "String", title:"IP of Amp Controller", description: "", required: true, displayDuringSetup: true
			input "port", "NUMBER", title:"port of Amp Controller", description: "", required: true, displayDuringSetup: true
			input "Zone1Name", "String", title:"Name Of Zone 1", description: "", required: true, defaultValue: "Zone_1"
			input "Zone2Name", "String", title:"Name Of Zone 2", description: "", required: true, defaultValue: "Zone_2"
			input "Zone3Name", "String", title:"Name Of Zone 3", description: "", required: true, defaultValue: "Zone_3"
			input "Zone4Name", "String", title:"Name Of Zone 4", description: "", required: true, defaultValue: "Zone_4"
			input "Zone5Name", "String", title:"Name Of Zone 5", description: "", required: true, defaultValue: "Zone_5"
			input "Zone6Name", "String", title:"Name Of Zone 6", description: "", required: true, defaultValue: "Zone_6"
            input "Zone7Name", "String", title:"Name Of Zone 7", description: "", required: true, defaultValue: "Zone_7"
            input "Zone8Name", "String", title:"Name Of Zone 8", description: "", required: true, defaultValue: "Zone_8"
            input "Zone9Name", "String", title:"Name Of Zone 9", description: "", required: true, defaultValue: "Zone_9"
            input "Zone10Name", "String", title:"Name Of Zone 10", description: "", required: true, defaultValue: "Zone_10"
            input "Zone11Name", "String", title:"Name Of Zone 11", description: "", required: true, defaultValue: "Zone_11"
            input "Zone12Name", "String", title:"Name Of Zone 12", description: "", required: true, defaultValue: "Zone_12"
            input "Zone13Name", "String", title:"Name Of Zone 13", description: "", required: true, defaultValue: "Zone_13"
            input "Zone14Name", "String", title:"Name Of Zone 14", description: "", required: true, defaultValue: "Zone_14"
            input "Zone15Name", "String", title:"Name Of Zone 15", description: "", required: true, defaultValue: "Zone_15"
            input "Zone16Name", "String", title:"Name Of Zone 16", description: "", required: true, defaultValue: "Zone_16"
            input "Zone17Name", "String", title:"Name Of Zone 17", description: "", required: true, defaultValue: "Zone_17"
            input "Zone18Name", "String", title:"Name Of Zone 18", description: "", required: true, defaultValue: "Zone_18"
            input "Channel1Name", "String", title:"Name of channel 1", description: "", required: true, defaultValue: "Channel1"
            input "Channel2Name", "String", title:"Name of channel 2", description: "", required: true, defaultValue: "Channel2"
            input "Channel3Name", "String", title:"Name of channel 3", description: "", required: true, defaultValue: "Channel3"
            input "Channel4Name", "String", title:"Name of channel 4", description: "", required: true, defaultValue: "Channel4"
            input "Channel5Name", "String", title:"Name of channel 5", description: "", required: true, defaultValue: "Channel5"
            input "Channel6Name", "String", title:"Name of channel 6", description: "", required: true, defaultValue: "Channel6"
			input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
			input name: "NumberAmps", type: "enum", description: "", title: "Number Amps", options: [[1:"1"],[2:"2"],[3:"3"]], defaultValue: 1
			input name: "PollMinutes", type: "Number", description: "", title: "Poll frequency in min", defaultValue: 1, range: "0..59"
			// 1, 5, 15 and 30 minites
		}
	}
}
def Unschedule(){
	if (logEnable) log.debug "Parent unschedule"
	unschedule()
}
def setChildzones(){
	if (logEnable) log.debug "Parent setChildzones"
	def children = getChildDevices()
	children.each {child->
		child.setZone()
	}
}
def recreateChildDevices() {
	if (logEnable) log.debug "Parent recreateChildDevices"
    deleteChildren()
    createChildDevices()
}
def createChildDevices() {
	log.debug "Parent createChildDevices ${settings.NumberAmps}"
	addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-11", [name: "child-${Zone1Name}", label: "${settings.Zone1Name}", zone: 11, isComponent: false])
	addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-12", [name: "child-${Zone2Name}", label: "${settings.Zone2Name}", zone: 12, isComponent: false])
	addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-13", [name: "child-${Zone3Name}", label: "${settings.Zone3Name}", zone: 13, isComponent: false])
	addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-14", [name: "child-${Zone4Name}", label: "${settings.Zone4Name}", zone: 14, isComponent: false])
	addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-15", [name: "child-${Zone5Name}", label: "${settings.Zone5Name}", zone: 15, isComponent: false])
	addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-16", [name: "child-${Zone6Name}", label: "${settings.Zone6Name}", zone: 16, isComponent: false])
    if (settings.NumberAmps.toInteger() > 1){
        if (logEnable) log.debug "Parent: 2 amp ceating 2nd set of childerens"
        addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-21", [name: "child-${Zone7Name}", label: "${settings.Zone7Name}", zone: 21, isComponent: false])
	    addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-22", [name: "child-${Zone8Name}", label: "${settings.Zone8Name}", zone: 22, isComponent: false])
	    addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-23", [name: "child-${Zone9Name}", label: "${settings.Zone9Name}", zone: 23, isComponent: false])
	    addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-24", [name: "child-${Zone10Name}", label: "${settings.Zone10Name}", zone: 24, isComponent: false])
	    addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-25", [name: "child-${Zone11Name}", label: "${settings.Zone11Name}", zone: 25, isComponent: false])
	    addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-26", [name: "child-${Zone12Name}", label: "${settings.Zone12Name}", zone: 26, isComponent: false])
    }
    if (settings.NumberAmps.toInteger() == 3){
        if (logEnable) log.debug "Parent: 3 amp ceating 3er set of childerens"
        addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-31", [name: "child-${Zone13Name}", label: "${settings.Zone13Name}", zone: 31, isComponent: false])
	    addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-32", [name: "child-${Zone14Name}", label: "${settings.Zone14Name}", zone: 32, isComponent: false])
	    addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-33", [name: "child-${Zone15Name}", label: "${settings.Zone15Name}", zone: 33, isComponent: false])
	    addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-34", [name: "child-${Zone16Name}", label: "${settings.Zone16Name}", zone: 34, isComponent: false])
	    addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-35", [name: "child-${Zone17Name}", label: "${settings.Zone17Name}", zone: 35, isComponent: false])
	    addChildDevice("jorge.martinez","MonoPrice 6 Zone Amp Controller - Child", "MP6ZA-child-36", [name: "child-${Zone18Name}", label: "${settings.Zone18Name}", zone: 36, isComponent: false])
    }     
	setChildzones ()
}
/*
def createChildDevices() {
	log.debug "Parent createChildDevices"
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-11", [name: "child-${Zone1Name}", label: "${settings.Zone1Name}", zone: 11, isComponent: false])
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-12", [name: "child-${Zone2Name}", label: "${settings.Zone2Name}", zone: 12, isComponent: false])
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-13", [name: "child-${Zone3Name}", label: "${settings.Zone3Name}", zone: 13, isComponent: false])
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-14", [name: "child-${Zone4Name}", label: "${settings.Zone4Name}", zone: 14, isComponent: false])
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-15", [name: "child-${Zone5Name}", label: "${settings.Zone5Name}", zone: 15, isComponent: false])
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-16", [name: "child-${Zone6Name}", label: "${settings.Zone6Name}", zone: 16, isComponent: false])
	setChildzones ()
}*/

def deleteChildren() {
	if (logEnable) log.debug "Parent deleteChildren"
	def children = getChildDevices()
    children.each {child->
  		deleteChildDevice(child.deviceNetworkId)
    }
}
def CloseTelnet(){
    if (logEnable) log.debug "Closing telnet"
    telnetClose() 
	unschedule()
}
def installed() {
	log.info('Parent MonoPrice 6 Zone Amp Controller: installed()')
	createChildDevices()
	initialize()
}
def updated(){
	log.info('Parent MonoPrice 6 Zone Amp Controller: updated()')
	initialize()
	//recreateChildDevices()
}
def pollSchedule(){
    forcePoll()
}
def initialize(){
	log.info('Parent MonoPrice 6 Zone Amp Controller: initialize()')
	telnetClose()
    try {
	    telnetConnect([termChars:[13]], settings.IP, settings.port as int, '', '')
    } catch (e) {
        sendEvent(name: 'networkStatus', value: "offline")
        throw e
    }
    sendEvent(name: 'networkStatus', value: "online")
    unschedule()
    runEvery5Minutes(healthCheck)
	schedulePoll()
	forcePoll()
}

void schedulePoll() {
    unschedule()
    Random rnd = new Random()
    if (settings.PollMinutes > 0) schedule( "${rnd.nextInt(59)} */${ settings.PollMinutes } * ? * *", "pollSchedule" )
    runEvery5Minutes(healthCheck)
}

def pollAmp1 (){
    if (logEnable) log.debug "Polling First 6 zones"
    sendMsg("?10")
}
def pollAmp2 (){
    if (logEnable) log.debug "Polling second 6 zones"
    sendMsg("?20")
}
def pollAmp3 (){
    if (logEnable) log.debug "Polling third 6 zones"
    sendMsg("?30")
}

def forcePoll(){
    if (logEnable) "forcePoll"
    runIn(1,"pollAmp1")
    if (settings.NumberAmps.toInteger() > 1){
        runIn(4,"pollAmp2")
    }
    if (settings.NumberAmps.toInteger() == 3){
        runIn(7,"pollAmp3")
    }
/*	if (logEnable) log.debug "Polling"
	sendMsg("?10")*/
}
def poll(){forcePoll()}

def sendMsg(String msg){
    state.lastTx = now()
	if (logEnable) log.debug ("Sending telnet msg: " + msg)
    def hubAction = new hubitat.device.HubAction(msg, hubitat.device.Protocol.TELNET)
    if (logEnable) log.debug "result: ${hubAction}"
    return hubAction;
}
private parse(String msg) {
    state.lastRx = now()
    if ((device.currentState("networkStatus").value == "offline")) healthCheck()
	if (logEnable) log.debug("Parse recive: " + msg)
	//if (!(msg.contains("Command Error")) && (msg.length()>5) && (msg.startsWith("#>"))){
    if (msg.substring(1,3)==("#>")) {
        def children = getChildDevices()
	    children.each {child->
		    if (msg.substring(3,5).toInteger() == child.currentValue("zone")){
			    child.UpdateData (msg)
			    if (logEnable) log.debug ("found mach: "+ msg)
		}
	  }
	}
}
def telnetStatus(String status){
	log.warn "telnetStatus: error: " + status
	if (status != "receive error: Stream is closed") {
		log.error "Connection was dropped."
		initialize()
	}
}

def getChanelName (Number channel){
    def channelName = null
    if (channel == 1) channelName = Channel1Name
    if (channel == 2) channelName = Channel2Name
    if (channel == 3) channelName = Channel3Name
    if (channel == 4) channelName = Channel4Name
    if (channel == 5) channelName = Channel5Name
    if (channel == 6) channelName = Channel6Name
    return channelName
}

void healthCheck() {
    // Should have received a response from poll + 1 minute and network should be connected
    def lastRxThreshold = ((settings.PollMinutes ?: 30) + Math.min(settings.PollMinutes, 5)) * 60 * 1000 // millseconds
    boolean networkConnected = (device.currentState("networkStatus").value == "online")
    
    if (state.lastRx == 0 || state.lastRx == null) {
        sendEvent(name: 'healthStatus', value: 'unknown')
    } else {
        boolean lastRxHealth = (now() - state.lastRx.toLong()) < lastRxThreshold
        if ((!networkConnected || !lastRxHealth)) log.debug "network = ${networkConnected} (${device.currentState("networkStatus").value}), rxHealth = ${lastRxHealth}: (${now() - state.lastRx.toLong()} < ${lastRxThreshold} @ ${settings.PollSchedule.toInteger()})"
        String healthStatus = unknownState ? 'unknown' : ((networkConnected && lastRxHealth) ? 'online' : 'offline')
        sendEvent(name: 'healthStatus', value: healthStatus)
    }
}
