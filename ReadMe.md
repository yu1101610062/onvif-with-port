因为项目上控制onvif的摄像头有时会采用非标准端口号，同时有的摄像头因为80端口转发控制端口是无法使用的(可能是现场无法使用80端口，也可能是摄像头不支持转发)，所以魔改了一下onvif的控制代码，加入了自定义端口的功能，入口在Main，引入onvif.jar依赖的原因是有好多onvif的dto太多了，懒得挨个反编译了，只拿出了基本流程的类在com.yyzy.pz中