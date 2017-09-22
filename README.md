# JavaTemplateEngine
Simple Java code TemplateEngine, use xml.

一个简易(功能的意义上)的模板引擎，用于根据xml文件描述的类结构生成java代码文件。

1. 使用xml描述类结构，不同于置换型模板引擎，不需要声明导包只需要描述结构，引擎会管理自动导包。
2. 包含一个简易(功能意义上的)置换型模板引擎，可以对xml或者任意字符串进行变量置换，支持添加分隔符、首字母大小写、全大小写 语法。
3. 包含一个格式化引擎，通过语法分析进行自动代码格式化。
4. 通过配置文件定义待使用类。

## 申明：2.0.5是fork自[Yumenokanata](https://github.com/Yumenokanata/JavaTemplateEngine)

## 2.0.6：
1.修正modifier支持
2.修正abstract支持
3.增加field的值

## 2.0.7
增加支持成员变量、内部类、方法的注释

## 使用方法

Demo：indi.yume.daggergenerator.example.Test11.java
```java
//声明一个字符串置换引擎
VarStringEngine varStringEngine = new VarStringEngine();
//绑定一个变量
varStringEngine.binding("name", "A09_1_TestTest");
//新建模板引擎，需要配置文件和一个字符串置换引擎，此时会根据这两个文件生成一个类的图，在之后渲染模板时进行类分析
TemplateEngine templateEngine = new TemplateEngine(new File("config.xml"), varStringEngine);
//设定模板文件，分析此文件并初始化类生成器
templateEngine.setTemplateFile(new File("activity.xml"));
//使用类生成器生成代码
String content = templateEngine.render();
```

## XML描述文件结构

### 配置文件

```xml
<!-- 基本属性（注解、类、接口都相同） -->
<!-- (必须)name 此引用类的引用名，在classVarName属性中调用 -->
<!-- (必须)packageName 所在包 -->
<!-- (必须)className 类名 -->
<!-- (可选)modifier 修饰符，如private、static等 -->
<!-- (可选)isInner 是否为内部类（导包时的处理方式不同） -->

<!-- config为根节点 -->
<config>
    <!-- annotation 声明一个注解引用  -->
    <annotation name="inject" packageName="javax.inject" className="Inject"/>
    <!-- class 声明一个类引用  -->
    <!-- 不需要导包时将packageName属性设置为空即可  -->
    <class name="string" packageName="" className="String"/>
    <!-- interface 声明一个接口引用  -->
    <interface isInner="true" name="inject" packageName="javax.inject" className="Inject"/>

    <!-- generic 声明一个此引用类的泛型（可用于接口和普通类）  -->
    <class name="map" packageName="" className="Map">
      <!-- 单个泛型 -->
      <generic classVarName="string">
      <!-- 多个泛型（两种方式可混合使用） -->
      <generic>
        <item classVarName="string">
        <item classVarName="string">
      </generic>
    </class>
</config>
```

### 类描述文件

***classVarName 全为配置文件中 name属性的设置***

###### Example:

```xml
package com.happy_bears.mybears.android.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.happy_bears.mybears.android.R;
import com.happy_bears.mybears.android.ui.AppComponent;
import com.happy_bears.mybears.android.ui.component.A09_1_TestTestComponent;
import com.happy_bears.mybears.android.ui.component.DaggerA09_1_TestTestComponent;
import com.happy_bears.mybears.android.ui.module.A09_1_TestTestModule;
import com.happy_bears.mybears.android.ui.presenter.A09_1_TestTestPresenter;

import java.util.concurrent.atomic.AtomicInteger.AtomicInteger;

import javax.inject.Inject;
/**
* Created by DaggerGenerator on 2017/09/22.
* test
*/
private static class A09_1_TestTestFragment extends BaseFragment implements A09_1_TestTestPresenter.A09_1_TestTestListener{
    @Inject
    A09_1_TestTestPresenter presenter;
    public A09_1_TestTestPresenter presenter1;
    protected A09_1_TestTestPresenter presenter2;
    private A09_1_TestTestPresenter presenter3;
    /**
    * sadfsdfs
    * dafasdfasdfdafd
    * adsfasdf
    */
    private volatile A09_1_TestTestPresenter presenter4;
    public static final String CONSTANT = "constan_value";
    private AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public void onCreateView(Bundle savedInstanceState){
        setContentView(R.layout.a09_1_test_test_fragment);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void inject(AppComponent appComponent){
        A09_1_TestTestComponent homeComponent = DaggerA09_1_TestTestComponent.builder()
            .appComponent(appComponent)
            .a09_1_TestTestModule(new A09_1_TestTestModule(this))
            .build();
        homeComponent.injectActivity(this);
        homeComponent.injectPresenter(presenter);
    }

    private static Intent createIntent(Context context){
        return new Intent(context, A09_1_TestTestActivity.class);
    }

    static Intent createIntent1(Context context){
        return new Intent(context, A09_1_TestTestActivity.class);
    }

    abstract Intent createIntent2();

    private static final synchronized Intent createIntent3(Context context){
        return new Intent(context, A09_1_TestTestActivity.class);
    }

    private void initView();

    private void initData(){
        
    }

    /**
    * innerClasss
    * adsfasdf
    */
    private static class A09_1_TestTestFragment extends BaseFragment implements A09_1_TestTestPresenter.A09_1_TestTestListener{

        private void initData(){
            
        }

    }
}
```

```
<classMaker classVarName="fragment" modifier="abstract">
    <extends classVarName="baseFragment" />
    <implements classVarName="presenterGetInterface" />

    <note>${note}</note>

    <property classVarName="presenter" valueName="presenter">
        <anno classVarName="inject" />
    </property>
    <property classVarName="presenter" modifier="public" valueName="presenter1"></property>
    <property classVarName="presenter" modifier="protected" valueName="presenter2"></property>

    <property classVarName="presenter" modifier="private" valueName="presenter3"></property>
    <property classVarName="presenter" modifier="private#volatile" valueName="presenter4">
        <note>
            sadfsdfs\ndafasdfasdfdafd\nadsfasdf
        </note>
    </property>
    <property classVarName="string" modifier="public#static#final" valueName="CONSTANT">
        <body>
            "constan_value"
        </body>
    </property>

    <property classVarName="atomicInteger" modifier="private" valueName="atomicInteger">
        <body>
            new AtomicInteger(1)
        </body>

    </property>

    <method methodName="onCreateView" modifier="public" returnClassName="void">
        <anno classVarName="override" />

        <methodParam classVarName="bundle" valueName="savedInstanceState" />

        <include classVarName="R" />
        <body>
            setContentView(R.layout.${_-name}_fragment);\n
            super.onCreate(savedInstanceState);\n

        </body>
    </method>

    <method methodName="inject" modifier="public" returnClassName="void">
        <anno classVarName="override" />

        <methodParam classVarName="appComponent" valueName="appComponent" />

        <include classVarName="dagger" />
        <include classVarName="module" />
        <include classVarName="component" />
        <body>
            ${name}Component homeComponent = Dagger${name}Component.builder()\n
            .appComponent(appComponent)\n
            .${&lt;name}Module(new ${name}Module(this))\n
            .build();\n
            homeComponent.inject${type}(this);\n
            homeComponent.injectPresenter(presenter);
        </body>
    </method>
    <method methodName="createIntent" modifier="private#static" returnClassName="intent">

        <methodParam classVarName="context" valueName="context" />
        <body>
            return new Intent(context, ${name}Activity.class);
        </body>
    </method>
    <method methodName="createIntent1" modifier="static" returnClassName="intent">

        <methodParam classVarName="context" valueName="context" />
        <body>
            return new Intent(context, ${name}Activity.class);
        </body>
    </method>
    <method methodName="createIntent2" modifier="abstract" returnClassName="intent"></method>

    <method methodName="createIntent3" modifier="private#static#final#synchronized"
        returnClassName="intent">
        <methodParam classVarName="context" valueName="context" />
        <body>
            return new Intent(context, ${name}Activity.class);
        </body>
    </method>

    <method methodName="initView" modifier="private" returnClassName="void"></method>

    <method methodName="initData" modifier="private" returnClassName="void">
        <note>
            sadfsdfsdafasdfasdfdafd\nadsfasdf
        </note>
        <body></body>
    </method>

    <classMaker classVarName="fragment" isInner="true" modifier="private#static">
        <extends classVarName="baseFragment" />
        <implements classVarName="presenterGetInterface" />
        <note>innerClasss\nadsfasdf</note>
        <method methodName="initData" modifier="private" returnClassName="void">
            <note>
                sadfsdfsdafasdfasdfdafd\nadsfasdf
            </note>
            <body></body>
        </method>
    </classMaker>
</classMaker>
```


## 单独使用

字符串置换引擎、代码格式化引擎两个子模块也可单独使用

### 字符串置换引擎（VarStringEngine）

```java
//声明一个字符串置换引擎
VarStringEngine varStringEngine = new VarStringEngine();
//绑定一个变量
varStringEngine.binding("name", "A09_1_TestTest");
String oriString = "hello, ${_<name}";
varStringEngine.binding("name", "TestTest");
String string = varStringEngine.analysisString(oriString); // "hello, test_Test"
```

#### 语法
  关键字：- + < >
  1. -
  全部字母小写
  2. +
  全部字母大写
  3. <
  首字母小写
  4. \>
  首字母大写

  其中以关键字为分割，前面如果有内容则以前面的内容作为分隔符对字符串进行分割
  如"${&&&+name}"定义name="TestTest&&&Test"则输出："TEST&&&TEST&&&TEST"

### 代码格式化引擎（StringContentEngine）

通过一个堆栈分析字符串中的语法成分（主要是利用"{}"与"()"的层数与其他一些语法规则）来进行代码的格式化
#### 用法
```java
StringContentEngine.generateString(String startString, String tabString, String content);
```