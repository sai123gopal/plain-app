import{_ as v}from"./TagFilter.vuevuetypescriptsetuptruelang-8f4bf8cc.js";import{d as g,n as C,p as k,q as w,o as N,c as $,e as s,v as n,g as t,b as e,f as i,w as l,x as _,z as p}from"./index-71bc54e4.js";import{g as m,M as y}from"./splitpanes.es-9bf0a422.js";import{p as z}from"./search-cd166c4f.js";import"./index-92f07970.js";import"./EditValueModal.vuevuetypescriptsetuptruelang-f6bb53bb.js";import"./VModal.vuevuetypescriptsetuptruelang-1499a382.js";import"./vee-validate.esm-b28bbd9f.js";import"./DeleteConfirm.vuevuetypescriptsetuptruelang-131cf6c7.js";import"./baseIndexOf-70b929c6.js";const B={class:"page-container container-fluid"},M={class:"sidebar"},S={class:"nav-title"},T={class:"nav"},V=["onClick"],b=["onClick"],q={class:"main"},J=g({__name:"NotesRootView",setup(E){const o=C(),r=k(),c=z(o.query);function d(){p(r,"/notes/trash")}function u(){p(r,"/notes")}return(a,R)=>{const f=v,h=w("router-view");return N(),$("div",B,[s(t(y),null,{default:n(()=>[s(t(m),{size:"20"},{default:n(()=>[e("div",M,[e("h2",S,i(a.$t("page_title.notes")),1),e("ul",T,[e("li",{onClick:l(u,["prevent"]),class:_({active:t(o).path==="/notes"&&!t(c)})},i(a.$t("all")),11,V),e("li",{onClick:l(d,["prevent"]),class:_({active:t(o).path==="/notes/trash"})},i(a.$t("trash")),11,b)]),s(f,{"tag-type":"NOTE",selected:t(c)},null,8,["selected"])])]),_:1}),s(t(m),null,{default:n(()=>[e("div",q,[s(h)])]),_:1})]),_:1})])}}});export{J as default};