import template from './app.html';
import 'common/bootstrap-overrides.less'
import './app.less';

let appComponent = ()=>{
	return {
		template, // because we have a variable name template we can use the shorcut here
		restrict: 'E'
	};
};

export default appComponent;
