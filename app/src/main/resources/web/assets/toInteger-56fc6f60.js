import{ba as s,av as i}from"./index-2128710d.js";var o=/\s/;function c(r){for(var t=r.length;t--&&o.test(r.charAt(t)););return t}var I=/^\s+/;function m(r){return r&&r.slice(0,c(r)+1).replace(I,"")}var e=0/0,p=/^[-+]0x[0-9a-f]+$/i,a=/^0b[01]+$/i,b=/^0o[0-7]+$/i,d=parseInt;function y(r){if(typeof r=="number")return r;if(s(r))return e;if(i(r)){var t=typeof r.valueOf=="function"?r.valueOf():r;r=i(t)?t+"":t}if(typeof r!="string")return r===0?r:+r;r=m(r);var n=a.test(r);return n||b.test(r)?d(r.slice(2),n?2:8):p.test(r)?e:+r}var f=1/0,N=17976931348623157e292;function x(r){if(!r)return r===0?r:0;if(r=y(r),r===f||r===-f){var t=r<0?-1:1;return t*N}return r===r?r:0}function T(r){var t=x(r),n=t%1;return t===t?n?t-n:t:0}export{T as a,y as t};